"""
Find the DOTA 2 teams with the most combined player score,
where score is defined as the length of a player's recorded history.
"""
import heapq
import logging as log
import sys
from argparse import ArgumentParser, FileType
from datetime import datetime
from operator import itemgetter
from sys import stdout
from typing import List, Tuple, Dict, Any, TextIO

import requests
import yaml


class Skore:
    """
    Finds the top DOTA 2 teams with most combined player score
    """

    def __init__(self, api_base_url: str, output_file: TextIO = None) -> None:
        """
        Initialize the score calculation

        :param api_base_url: the URL base for the API server
        :param output_file: the output file for printing the results
        :return: None
        """
        # Combined score map of teams
        self.team_score: Dict[int, Any] = {}
        self.api_base_url: str = api_base_url
        self.output_file: TextIO = output_file

    def print_top_score_teams(self, num_teams: int) -> None:
        """
        Find and print the given number of team data with the highest combined score.

        :param num_teams: the expected number of teams in result to be printed
        :return: None
        """
        log.info('Printing top %d team(s) to result yaml %s', num_teams, self.output_file.name)
        with self.output_file:
            for team in self.calculate_top_teams(self.__get_players(), self.__get_teams(), num_teams):
                yaml.dump({team: self.team_score[team]}, self.output_file, default_flow_style=False)

    def get_top_score_teams(self, num_teams: int) -> List:
        """
        Find the given number of team data with the highest combined score.

        :param num_teams: the expected number of teams in result
        :return: None
        """
        log.info('Getting top %d team(s)', num_teams)
        result: List[Any] = []
        for team in self.calculate_top_teams(self.__get_players(), self.__get_teams(), num_teams):
            result.append(self.team_score[team])
        return result

    def calculate_top_teams(self, players: list, teams: list, num_teams: int) -> List[int]:
        """
        Find the given number of team ids with the highest score.

        :param players: the players list json
        :param teams: the teams list json
        :param num_teams: the expected number of teams in list
        :return: the list of ids of the top teams
        """
        log.info('Finding top %d team(s) from %s', num_teams, self.api_base_url)
        if num_teams < 1:
            log.error('Please provide a valid top number of teams instead of the provided %d', num_teams)
            return []
        if not players or not teams:
            log.error('Invalid players/teams provided')
            return []
        self.__calculate_team_score(players)
        self.__update_team_info(teams)
        return self.__find_top_teams(num_teams)

    def __find_top_teams(self, num_teams: int) -> List[int]:
        """
        Find the given number of teams with the highest combined score from the already built team score map.
        TODO: Measure/understand Python heap/sorted performance for fine tune the top k of n search

        :param num_teams: the expected number of teams in result
        :return: the list of ids of the top teams
        """
        # When result size is one optimize with linear search.  O(n)
        if num_teams == 1:
            log.debug('Using linear search for single result out of %d', len(self.team_score))
            max_score = 0
            team_with_max_score = 0
            for key, value in self.team_score.items():
                if value['xp'] > max_score:
                    max_score = value['xp']
                    team_with_max_score = key
            return [team_with_max_score]
        # When the result size is larger use in place sorting without additional space. O(n log n)
        if num_teams >= len(self.team_score) / 2:
            log.debug('Using sorted for finding %d largest out of %d', num_teams, len(self.team_score))
            return sorted(self.team_score, key=lambda x: self.team_score[x]['xp'], reverse=True)[:num_teams]
        # When the result size k is smaller use a max heap. O(n + k log n) and O(n) space
        log.debug('Using max heap for finding %d largest out of %d', num_teams, len(self.team_score))
        score_id: List[Tuple[int, int]] = []
        # Create a list of (xp, id) for the teams from team_score
        for key, value in self.team_score.items():
            score_id.append((value['xp'], key))
        # Use heap to find largest team scores from score_id using id (score_id[0]) as key,
        # then map and return the corresponding ids (score_id[1]) as a list
        return list(map(itemgetter(1), heapq.nlargest(num_teams, score_id, key=itemgetter(0))))

    def __calculate_team_score(self, players: list) -> None:
        """
        Calculate team score combining player scores.

        Players with invalid account id and/or team id and/or history time will be excluded from calculation
        Other player and team attributes are optional and any invalid attribute values will be ignored
        and defaults (0 for numbers and '' for strings) will be used.

        Optional team attributes are initialized with defaults to be updated later in __update_team_info().
        :param players:  list of players
        :return: None
        """
        for player in players:
            if player['team_id'] > 0 and player['account_id'] > 0:
                try:
                    player_score = Skore.calculate_score(player['full_history_time'])
                except ValueError:
                    log.debug(
                        'Excluding invalid player %d time %s', player['account_id'], player['full_history_time'])
                    # Exclude players with invalid history time from team calculation
                    continue
                self.team_score.setdefault(player['team_id'],
                                        {'name': '', 'team_id': player['team_id'], 'wins': 0, 'losses': 0, 'rating': 0,
                                         'players': []})
                self.team_score[player['team_id']]['players'].append(
                    {'personaname': player['personaname'], 'xp': player_score, 'country_code': player["country_code"]})
                if 'xp' in self.team_score[player['team_id']]:
                    self.team_score[player['team_id']]['xp'] += player_score
                else:
                    self.team_score[player['team_id']]['xp'] = player_score
            else:
                log.debug('Excluding invalid player %d team %d', player['account_id'], player['team_id'])

    def __update_team_info(self, teams: list) -> None:
        """
        Update the team score map with the optional team attributes.

        :param teams:  the list of teams
        :return: None
        """
        for team in teams:
            if team['team_id'] > 0 and team['team_id'] in self.team_score.keys():
                self.team_score[team['team_id']]['name'] = team['name']
                self.team_score[team['team_id']]['wins'] = team['wins']
                self.team_score[team['team_id']]['losses'] = team['losses']
                self.team_score[team['team_id']]['rating'] = team['rating']

    def __get_players(self) -> list:
        """
        Get the list of players from API server.

        :return: list of players
        """
        players = self.__get('proPlayers')
        if players is None:
            log.critical("Found no valid players, please check the logs")
            sys.exit(1)
        else:
            return players

    def __get_teams(self) -> list:
        """
        Get the list of teams from API server.

        :return:  list of teams
        """
        teams = self.__get('teams')
        if teams is None:
            log.critical("Found no valid teams, please check the logs")
            sys.exit(1)
        else:
            return teams

    def __get(self, api: str) -> list:
        """
        Get the response for the given API call.

        :param api: the API to call
        :return: the API response
        """
        api_url = '{0}/{1}'.format(self.api_base_url, api)
        try:
            return Skore.process_response(requests.get(api_url))
        except requests.exceptions.RequestException as exception:
            log.error('There was an error connecting to %s, please correct the URL or retry', api_url)
            log.exception(exception)

    @staticmethod
    def calculate_score(timestamp: str) -> int:
        """
        Calculate a players score based  on the amount of time that has passed since the start of a player's data.

        :param timestamp:  the timestamp when a players data started
        :return: the calculated score of the player
        """
        timestamp = str(timestamp)
        if timestamp is None or not timestamp.strip():
            raise ValueError('Timestamp should not be None or empty')
        hist_time = datetime.strptime(timestamp, "%Y-%m-%dT%H:%M:%S.%fZ")
        # Using precision of seconds for xp calculation
        # TODO: Explore using minutes/hours precision to avoid int overflow
        #  for teams with large number of players with long history time
        return int((datetime.now() - hist_time).total_seconds())

    @staticmethod
    def process_response(response: requests.Response) -> list:
        """
        Process the given API response json body and check for valid format and response code.

        :param response: the API response to process
        :return: the json response body
        """
        if response.status_code == 200:
            try:
                return response.json()
            except ValueError as error:
                log.error('Unexpected response format')
                log.exception(error)
        else:
            log.error('Unexpected response code %s', response)
            return []


def parse_args():
    """
    Retrieve args from command line.
    """
    parser = ArgumentParser(
        description='Find the DOTA 2 teams with the most combined player *score',
        epilog='*Experience is defined as the length of a player\'s recorded history.',
    )
    parser.add_argument(
        'output',
        type=FileType('w'),
        nargs='?',
        default=stdout,
        help='result output file. (default: stdout)'
    )
    parser.add_argument(
        '-n',
        '--numteams',
        type=int,
        default=5,
        help='number of teams in output. (default: %(default)s)'
    )
    parser.add_argument(
        '-l',
        '--loglevel',
        choices=['CRITICAL', 'ERROR', 'WARNING', 'INFO', 'DEBUG'],
        default='WARNING',
        help='Only output log messages of this severity or above. Writes to stderr. (default: %(default)s)'
    )
    parser.add_argument(
        '-s',
        '--server',
        default='https://api.opendota.com/api',
        help='The API server base URL. (default: %(default)s)'
    )
    return parser.parse_args()


def main():
    """
    Main entry point with args processing and log initialization.
    """
    args = parse_args()
    log.basicConfig(format='%(asctime)s %(levelname)-8s [%(filename)s:%(lineno)d] - %(message)s',
                    level=log.getLevelName(args.loglevel))
    Skore(api_base_url=args.server, output_file=args.output).print_top_score_teams(args.numteams)


if __name__ == '__main__':
    main()
