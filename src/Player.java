import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

class Point {
	int x;
	int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}

class Monster extends Point {

	int id;
	int wallsLeft;

	public Monster(int x, int y, int id, int wallsLeft) {
		super(x, y);
		this.id = id;
		this.wallsLeft = wallsLeft;
	}

	public Monster(int x, int y, int id) {
		super(x, y);
		this.id = id;
		this.wallsLeft = -1;
	}

	void update(int x, int y, int wallsLeft) {
		this.x = x;
		this.y = y;
		this.wallsLeft = wallsLeft;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Monster other = (Monster) obj;
		if (id != other.id)
			return false;
		return true;
	}
}

class Node extends Monster {

	Node prevNode;
	int g;
	int h;
	int score = 0;

	public Node(int x, int y, int id, int g, int h, Node prevNode) {
		super(x, y, id);
		this.g = g;
		this.prevNode = prevNode;
		this.h = h;
	}

	public boolean isGoal(int width, int height) {
		if (id == 0 && x == width - 1)
			return true;
		if (id == 1 && x == 0)
			return true;
		if (id == 2 && y == height - 1)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Node: (" + x + "," + y + ") g:" + g + " h:" + h + " prev: "
				+ (prevNode == null ? "null" : "(" + prevNode.x + "," + prevNode.y + ")");
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}

class Wall extends Point {
	String orientation;

	public Wall(int x, int y, String orientation) {
		super(x, y);
		this.orientation = orientation;
	}
}

class Action {
	String action;
}

class Solution {
	List<Action> actions;

	Solution() {
		actions = new ArrayList<>();
	}
}

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	public final static boolean DEBUG = false;

	int width; // width of the board
	int height; // height of the board
	int playerCount; // number of players (2 or 3)
	int myId; // id of my player (0 = 1st player, 1 = 2nd player, ...)

	List<Monster> monsters = new ArrayList<>();
	String walls[][];

	public static void main(String args[]) {
		new Player().play();
	}

	public void play() {
		Scanner in = new Scanner(System.in);
		width = in.nextInt(); // width of the board
		height = in.nextInt(); // height of the board
		playerCount = in.nextInt(); // number of players (2 or 3)
		myId = in.nextInt(); // id of my player (0 = 1st player, 1 = 2nd player, ...)
		walls = new String[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				walls[i][j] = "";
			}
		}

		if (DEBUG) {
			Node startNode = new Node(0, 1, 0, 0, width, null);
			List<Node> nextNodes = computeNextNodes(startNode);
			// walls[1][1] = 'V';
			// walls[0][1] = 'H';
			// walls[5][3] = 'V';
			// walls[8][2] = 'V';
			long startTime = System.currentTimeMillis();
			Node targetNode = aStar(startNode);
			System.err.println("Finished in: " + (System.currentTimeMillis() - startTime));

			System.exit(0);
		}

		int turn = 0;
		// game loop
		while (true) {
			System.err.println("turn :  " + turn);
			for (int i = 0; i < playerCount; i++) {
				int x = in.nextInt(); // x-coordinate of the player
				int y = in.nextInt(); // y-coordinate of the player
				int wallsLeft = in.nextInt(); // number of walls available for the player
				if (turn == 0) {
					Monster monster = new Monster(x, y, i, wallsLeft);
					monsters.add(monster);
				} else {
					monsters.get(i).update(x, y, wallsLeft);
				}
			}

			int wallCount = in.nextInt(); // number of walls on the board
			for (int i = 0; i < wallCount; i++) {
				int wallX = in.nextInt(); // x-coordinate of the wall
				int wallY = in.nextInt(); // y-coordinate of the wall
				String wallOrientation = in.next(); // wall orientation ('H' or 'V')
				walls[wallX][wallY] = wallOrientation;
			}

			System.err.println("Monsters: " + monsters);
			for (int i = 1; i < walls.length; i++) {
				for (int j = 1; j < walls[0].length; j++) {
					System.err.print(walls[j][i] + "-");
				}
				System.err.println();
			}

			int worstScore = 0;
			Node myNextNode = null;
			Node targetNode = null;
			String orientation = "";
			String move = "";
			for (Monster monster : monsters) {
				if (monster.id == myId) {
					Node myNode = new Node(monster.x, monster.y, monster.id, 0, width, null);
					System.err.println("MY Starting node: " + myNode);
					myNextNode = aStar(myNode);
					System.err.println("myNextNode: " + myNextNode + " myScore ? " + myNextNode.score);
					move = getMove(myNode, myNextNode);
				} else if (monster.x != -1) {
					Node hisNode = new Node(monster.x, monster.y, monster.id, 0, width, null);
					System.err.println("Starting node for monster: " + monster.id + " is:" + hisNode);
					Node nextNode = aStar(hisNode);
					System.err.println("hisNextNode: " + nextNode + " score ? " + nextNode.score);
					if (nextNode.score > worstScore) {
						worstScore = nextNode.score;
						targetNode = nextNode;
						if (nextNode.x != hisNode.x) {
							orientation = "V";
						} else {
							orientation = "H";
						}
					}
				}
			}
			if (worstScore < myNextNode.score && monsters.get(myId).wallsLeft > 0) {
				// PUT V WALL
				if (orientation == "V" && !walls[targetNode.x][targetNode.y].contains("V")
						&& (targetNode.y < height - 1 && !walls[targetNode.x][targetNode.y + 1].contains(orientation))
						&& (targetNode.y < height - 1 && targetNode.x > 0
								&& !walls[targetNode.x - 1][targetNode.y + 1].contains("H"))) {
					System.out.println(targetNode.x + " " + targetNode.y + " " + orientation);
					// PUT H WALL
				} else if (orientation == "H" && targetNode.y != 0
						&& !walls[targetNode.x][targetNode.y].contains(orientation)
						&& (targetNode.x < width - 1 && !walls[targetNode.x + 1][targetNode.y].contains(orientation))
						&& (targetNode.y > 0 && targetNode.x < width - 1
								&& !walls[targetNode.x + 1][targetNode.y - 1].contains("V"))) {
					System.out.println(targetNode.x + " " + targetNode.y + " " + orientation);
				} else {
					// MOVE ANYWAY
					System.out.println(move);
				}
			} else {
				System.out.println(move);
			}
			turn++;
		}

	}

	private String getMove(Node myNode, Node myNextNode) {
		if (myNextNode.x < myNode.x) {
			return "LEFT";
		}
		if (myNextNode.x > myNode.x) {
			return "RIGHT";
		}
		if (myNextNode.y > myNode.y)
			return "DOWN";
		if (myNextNode.y < myNode.y)
			return "UP";
		return null;
	}

	public Node aStar(Node startNode) {
		// Open queue
		Queue<Node> openQueue = new PriorityQueue<>(new Comparator<Node>() {

			@Override
			public int compare(Node node1, Node node2) {
				return Integer.compare(node1.g + node1.h, node2.g + node2.h);
			}
		});

		// ClosedList
		List<Node> closedList = new ArrayList<>();

		openQueue.add(startNode);

		while (!openQueue.isEmpty()) {
			Node currentNode = openQueue.poll();
			if (DEBUG)
				System.err.println("Exploring node: " + currentNode);
			if (currentNode.isGoal(width, height)) {
				if (DEBUG)
					System.err.println("Reached final node at: (" + currentNode.x + "," + currentNode.y + ")");
				int score = 0;
				while (currentNode.prevNode.prevNode != null) {
					currentNode = currentNode.prevNode;
					score++;
				}
				currentNode.score = score;
				if (DEBUG)
					System.err.println("Target node is : " + currentNode + " score: " + currentNode.score);
				return currentNode;
			}

			for (Node nextNode : computeNextNodes(currentNode)) {
				if (closedList.contains(nextNode))
					continue;
				if (!openQueue.contains(nextNode)) {
					openQueue.add(nextNode);
				} else {
					Node nodeToRemove = null;
					for (Node oldNode : openQueue) {
						if (oldNode.equals(nextNode)) {
							if (oldNode.g + oldNode.h > nextNode.g + nextNode.h) {
								nodeToRemove = oldNode;
							}
							break;
						}
					}
					if (nodeToRemove != null) {
						openQueue.remove(nodeToRemove);
						openQueue.add(nextNode);
					}
				}
			}

			closedList.add(currentNode);

		}

		if (DEBUG)
			System.err.println("Could not find path ! returning null");
		return null;
	}

	private List<Node> computeNextNodes(Node currentNode) {
		List<Node> nextNodes = new ArrayList<>();
		int x = currentNode.x;
		int y = currentNode.y;
		if (x > 0 && !walls[x][y].contains("V") && (y == 0 || !walls[x][y - 1].contains("V"))) {
			nextNodes.add(createNode(currentNode, x - 1, y));
		}
		if (x < width - 1 && !walls[x + 1][y].contains("V") && (y == 0 || !walls[x + 1][y - 1].contains("V"))) {
			nextNodes.add(createNode(currentNode, x + 1, y));
		}
		if (y > 0 && !walls[x][y].contains("H") && (x == 0 || !walls[x - 1][y].contains("H"))) {
			nextNodes.add(createNode(currentNode, x, y - 1));
		}
		if (y < height - 1 && !walls[x][y + 1].contains("H") && (x == 0 || !walls[x - 1][y + 1].contains("H"))) {
			nextNodes.add(createNode(currentNode, x, y + 1));
		}
		if (DEBUG)
			System.err.println("NextNodes:  " + nextNodes);
		return nextNodes;
	}

	private Node createNode(Node currentNode, int x, int y) {
		int h = 0;
		if (currentNode.id == 0)
			h = width - x;
		else if (currentNode.id == 1)
			h = x;
		else
			h = height - y;
		return new Node(x, y, currentNode.id, currentNode.g + 1, h, currentNode);
	}
}