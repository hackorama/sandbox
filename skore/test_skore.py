"""
Skore unit tests.
"""
import json
import sys
import unittest

from skore import Skore


class TestSkore(unittest.TestCase):
    """
    Test cases for Skore.
    """

    def test_skore_calc(self):
        """
        Test the time string format handling when calculating experience.
        """
        self.assertGreater(Skore.calculate_score("2020-01-27T19:29:34.335Z"), 300000)
        self.assertRaises(ValueError, Skore.calculate_score, None)
        self.assertRaises(ValueError, Skore.calculate_score, '')
        self.assertRaises(ValueError, Skore.calculate_score, 'foo')
        self.assertRaises(ValueError, Skore.calculate_score, 0)

    def test_top_teams(self):
        """
        Test basic experience calculation.

        TODO: Add more error handling cases missing/illegal attributes
        """
        players = """
         [
           { "account_id": 1, "personaname": "test1", "full_history_time": "2010-01-27T19:29:34.335Z",
             "country_code": "cn", "team_id": 101 },
           { "account_id": 2, "personaname": "test2", "full_history_time": "2010-01-27T19:29:34.335Z",
             "country_code": "cn", "team_id": 102 },
           { "account_id": 3, "personaname": "test3", "full_history_time": "2015-01-27T19:29:34.335Z",
             "country_code": "cn", "team_id": 102 },
           { "account_id": 4, "personaname": "test4", "full_history_time": "2020-01-27T19:29:34.335Z",
             "country_code": "cn", "team_id": 103 }
         ]
        """
        teams = """
        [
          { "team_id": 101, "rating": 1.1, "wins": 1, "losses": 3, "name": "test101" },
          { "team_id": 102, "rating": 1.2, "wins": 2, "losses": 2, "name": "test102" },
          { "team_id": 103, "rating": 1.3, "wins": 3, "losses": 1, "name": "test103" }
        ]
        """
        # Team 102 : Player 2 from 2010 and Player 3 from 2015 - 25 year xp
        # Team 101 : Player 1 from 2010                        - 10 year xp
        # Team 103 : Player 3 from 2020                        - <1 year xp
        self.assertEqual([],
                         Skore("test", sys.stdout).calculate_top_teams(json.loads(players), json.loads(teams), 0))
        self.assertEqual([102],
                         Skore("test", sys.stdout).calculate_top_teams(json.loads(players), json.loads(teams), 1))
        self.assertEqual([102, 101],
                         Skore("test", sys.stdout).calculate_top_teams(json.loads(players), json.loads(teams), 2))
        self.assertEqual([102, 101, 103],
                         Skore("test", sys.stdout).calculate_top_teams(json.loads(players), json.loads(teams), 3))


if __name__ == '__main__':
    unittest.main()
