"""
Skore REST API web server
"""
from flask import Flask
from flask import jsonify
from flask_cors import CORS

from skore import Skore

app = Flask(__name__)

CORS(app, resources={r'/*': {'origins': '*'}})


@app.route('/', methods=['GET'])
@app.route('/<int:count>', methods=['GET'])
def score(count=5):
    """
    Team Score get request route handler

    TODO: Custom validation error message for path param <count>

    :param count: the expected number of teams in result
    :return: the list of top teams
    """
    return jsonify(Skore('https://api.opendota.com/api').get_top_score_teams(count))


if __name__ == '__main__':
    app.run(host='0.0.0.0')
