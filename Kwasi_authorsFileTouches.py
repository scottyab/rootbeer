import sys
import subprocess
import os

# Get the list of authors and times of those who touched each file
def get_authors():
    repo_path = os.path.expanduser('~/projects/rootbeer/rootbeer')
    output_file = 'authors.txt'

    with open(output_file, 'w', encoding='utf-8') as f:
        # Header 
        header = f"{'FILE PATH':<50} | {'AUTHOR':<20} | {'DATE'}\n"
        f.write(header)
        f.write("-" * 100 + "\n")

        seen = set()
        for line in sys.stdin:
            file_path = line.strip()
            
            # Skip lines that aren't actually file paths (like your summary prints)
            if not file_path or "Total number" in file_path or "The file" in file_path:
                continue

            if file_path in seen:
                continue
            seen.add(file_path)

            try:
                cmd = ['git', '-C', repo_path, 'log', '--format=%an|%ai', '--', file_path]
                output = subprocess.check_output(cmd, stderr=subprocess.STDOUT).decode('utf-8')
                
                output_lines = output.strip().split('\n')
                if not output_lines or output_lines == ['']:
                    continue

                for entry in output_lines:
                    if '|' in entry:
                        author, date = entry.split('|')
                        f.write(f"{file_path:<50} | {author:<20} | {date}\n")
                        # Force write to disk so see progress
                        f.flush()
                       
            except Exception as e:
                continue

    print(f"Done! Results saved to {output_file}", file=sys.stderr)

if __name__ == "__main__":
    get_authors()