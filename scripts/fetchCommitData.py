import requests
import argparse
import csv
import time
import sys
import getpass

def get_forks(owner, repo, headers):
    forks = []
    page = 1
    while True:
        url = f'https://api.github.com/repos/{owner}/{repo}/forks'
        params = {'page': page, 'per_page': 100}
        response = requests.get(url, headers=headers, params=params)
        if response.status_code != 200:
            print(f"Error fetching forks: {response.status_code}")
            sys.exit(1)
        data = response.json()
        if not data:
            break
        forks.extend(data)
        page += 1
        # Rate limit handling
        if 'X-RateLimit-Remaining' in response.headers:
            remaining = int(response.headers['X-RateLimit-Remaining'])
            if remaining < 10:
                reset_time = int(response.headers['X-RateLimit-Reset'])
                sleep_time = max(reset_time - time.time(), 0) + 10
                print(f"Rate limit approaching. Sleeping for {sleep_time} seconds.")
                time.sleep(sleep_time)
    return forks

def get_commits(owner, repo, headers, since=None):
    commits = []
    page = 1
    while True:
        url = f'https://api.github.com/repos/{owner}/{repo}/commits'
        params = {'page': page, 'per_page': 100}
        if since:
            params['since'] = since
        response = requests.get(url, headers=headers, params=params)
        if response.status_code != 200:
            print(f"Error fetching commits for {owner}/{repo}: {response.status_code}")
            return commits
        data = response.json()
        if not data:
            break
        commits.extend(data)
        page += 1
        # Rate limit handling
        if 'X-RateLimit-Remaining' in response.headers:
            remaining = int(response.headers['X-RateLimit-Remaining'])
            if remaining < 10:
                reset_time = int(response.headers['X-RateLimit-Reset'])
                sleep_time = max(reset_time - time.time(), 0) + 10
                print(f"Rate limit approaching. Sleeping for {sleep_time} seconds.")
                time.sleep(sleep_time)
    return commits

def get_commit_details(owner, repo, sha, headers):
    url = f'https://api.github.com/repos/{owner}/{repo}/commits/{sha}'
    response = requests.get(url, headers=headers)
    if response.status_code != 200:
        print(f"Error fetching commit {sha} details: {response.status_code}")
        return None
    return response.json()

def main():
    parser = argparse.ArgumentParser(description='Fetch commit data from GitHub forks.')
    parser.add_argument('username', help='GitHub username of the repository owner.')
    parser.add_argument('repo', help='Name of the repository.')
    parser.add_argument('--after-hash', '-a', help='Only include commits after this commit hash.')
    args = parser.parse_args()

    token = getpass.getpass('Enter your GitHub Personal Access Token (leave blank for unauthenticated requests): ')

    headers = {}
    if token:
        headers['Authorization'] = f'token {token}'

    after_timestamp = None
    if args.after_hash:
        commit_details = get_commit_details(args.username, args.repo, args.after_hash, headers)
        if not commit_details:
            print(f"Commit hash {args.after_hash} not found in {args.username}/{args.repo}")
            sys.exit(1)
        after_timestamp = commit_details['commit']['committer']['date']
        print(f"Only including commits after {args.after_hash} ({after_timestamp})")

    print(f"Fetching forks for {args.username}/{args.repo}...")
    forks = get_forks(args.username, args.repo, headers)
    print(f"Total forks found: {len(forks)}")

    csv_file = 'commit_data.csv'
    with open(csv_file, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['ForkID', 'CommitTimestamp', 'LinesChanged']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        for idx, fork in enumerate(forks, start=1):
            fork_id = f"fork{idx}"
            fork_owner = fork['owner']['login']
            fork_repo = fork['name']
            print(f"Processing {fork_owner}/{fork_repo} as {fork_id}...")
            commits = get_commits(fork_owner, fork_repo, headers, since=after_timestamp)
            for commit in commits:
                sha = commit['sha']
                commit_details = get_commit_details(fork_owner, fork_repo, sha, headers)
                if not commit_details:
                    continue
                timestamp = commit_details['commit']['committer']['date']
                stats = commit_details.get('stats', {})
                lines_changed = stats.get('total', 0)
                writer.writerow({
                    'ForkID': fork_id,
                    'CommitTimestamp': timestamp,
                    'LinesChanged': lines_changed
                })
    print(f"Data saved to {csv_file}")

if __name__ == '__main__':
    main()
