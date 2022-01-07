import requests
import sys

COOKIE_PATH = sys.argv[1]

cookie = None
with open(COOKIE_PATH, 'r') as f:
    cookie = f.readline().strip('\n')

response = requests.get('https://adventofcode.com/2021/leaderboard/private/view/793673.json', cookies={
    'session': cookie,
})
data = response.json()


days = {}
members = data["members"]

for (uid, member) in members.items():
    for (day, stats) in member["completion_day_level"].items():

        if int(day) not in days:
            days[int(day)] = []

        if '2' not in stats:
            continue

        diff = stats['2']['get_star_ts'] - stats['1']['get_star_ts']
        days[int(day)].append({
            'member': uid,
            'diff': diff
        })

days = list(days.items())
days.sort(key=lambda x: x[0])

scores = {}
scores_by_diff = {}
num_members = len(members)

for (day, stats) in days:
    print(f"Day: {day}")
    stats.sort(key=lambda x: x['diff'])
    score_day = {}
    diff_day = {}
    for i, stat in enumerate(stats):
        if stat['member'] not in diff_day:
            diff_day[stat['member']] = []
        print(f"\t{members[stat['member']]['name']}: {stat['diff']}")
        score_day[stat['member']] = 2*num_members - 2*i
        diff_day[stat['member']] = stat['diff']

    scores[day] = score_day
    scores_by_diff[day] = diff_day

scores_by_member = {}
diff_by_member = {}
for member in members.keys():
    scores_by_member[member] = 0
    diff_by_member[member] = []
    for (_, member_scores) in scores.items():
        if member in member_scores:
            scores_by_member[member] += member_scores[member]

    for (_, member_scores) in scores_by_diff.items():
        if member in member_scores:
            diff_by_member[member].append(member_scores[member])


print("\n\nMain Leaderboard:")
results = list(members.items())
results.sort(key=lambda x: -x[1]["local_score"])
for i, result in enumerate(results):
    print(f" {i+1}. {result[1]['name']}: {result[1]['local_score']}")


print("\n\nLeaderboard by part 2 diff in points:")
results = list(scores_by_member.items())
results.sort(key=lambda x: -x[1])
for i, result in enumerate(results):
    print(f" {i+1}. {members[result[0]]['name']}: {result[1]}")

print("\n\nLeaderboard by part 2 diff in time:")
results = list(filter(lambda x: len(x[1]) == 25, list(diff_by_member.items())))
results.sort(key=lambda x: sum(x[1]))
for i, result in enumerate(results):
    print(
        f" {i+1}. {members[result[0]]['name']}: {sum(result[1])}")
