import matplotlib.pyplot as plt
from datetime import datetime
from collections import defaultdict

INPUT_FILE = "authors.txt"

def read_authors_file(path):
    rows = []
    with open(path, "r", encoding="utf-8") as f:
        for i, line in enumerate(f):
            line = line.rstrip()

            # Skip header
            if i == 0:
                continue

            # Skip underline (only dashes)
            if set(line.strip()) == {"-"}:
                continue

            # Expect: FILE | AUTHOR | DATE
            parts = [p.strip() for p in line.split("|")]
            if len(parts) != 3:
                continue

            file_path, author, date_str = parts
            rows.append((file_path, author, date_str))
    return rows

def parse_git_date(date_str: str):
    date_str = date_str.strip()

    # Common git format: "YYYY-MM-DD HH:MM:SS -0800"
    try:
        return datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S %z")
    except ValueError:
        pass

    raise ValueError(f"Unrecognized date format: {date_str}")

def week_index(date_str, base_date):
    d = parse_git_date(date_str).date()
    return (d - base_date).days // 7

def main():
    rows = read_authors_file(INPUT_FILE)
    if not rows:
        raise RuntimeError("No valid rows found in authors.txt")

    # Base date
    dates = [parse_git_date(d).date() for _, _, d in rows]
    base = min(dates)

    # Map files to Y-axis
    files = sorted({f for f, _, _ in rows})
    file_to_y = {f: i for i, f in enumerate(files)}

    # Group by author
    by_author = defaultdict(list)
    for f, a, d in rows:
        x = file_to_y[f]          # files on X axis
        y = week_index(d, base)   # weeks on Y axis
        by_author[a].append((x, y))

    plt.figure(figsize=(12, 6))

    # Build a stable mapping ONCE
    authors = sorted(by_author.keys())          # stable order
    cmap = plt.get_cmap("tab10")                # strong distinct colors
    
    author_to_color = {author: cmap(i % 10) for i, author in enumerate(authors)}
    # Plot most frequent authors first, rarest last (on top)
    authors_sorted = sorted(by_author.keys(), key=lambda a: len(by_author[a]), reverse=True)

    for author in authors_sorted:
        pts = by_author[author]
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]

        plt.scatter(xs, ys, label=author, s=120, color=author_to_color[author], alpha=0.9, edgecolors="black", linewidths=0.8)

    plt.ylabel("Weeks since first touch")
    plt.xlabel("File index")
    plt.xlim(left=0, right=len(files))
    plt.ylim(0, max(y for pts in by_author.values() for _, y in pts) + 20)
    plt.title("Weeks vs Files (colored by author)")
    plt.legend(fontsize="small", ncol=2)
    plt.grid(True)
    plt.tight_layout()
    plt.savefig("Kwasi_scatterplot.png", dpi=300)
    plt.show()

if __name__ == "__main__":
    main()