#include <iostream>
#include <string>
#include <vector>
#include <algorithm>

using namespace std;

// ***********************************************************

static unsigned int g_seed;
inline void fast_srand(int seed) {
  //Seed the generator
  g_seed = seed;
}
inline int fastrand() {
  //fastrand routine returns one integer, similar output value range as C lib.
  g_seed = (214013*g_seed+2531011);
  return (g_seed>>16)&0x7FFF;
}
inline int fastRandInt(int maxSize) {
  return fastrand() % maxSize;
}
inline int fastRandInt(int a, int b) {
  return(a + fastRandInt(b - a));
}
inline double fastRandDouble() {
  return static_cast<double>(fastrand()) / 0x7FFF;
}
inline double fastRandDouble(double a, double b) {
  return a + (static_cast<double>(fastrand()) / 0x7FFF)*(b-a);
}

// ****************************************************************************************

class Point {
public:
    int x;
    int y;
    Point() {};
    Point(int x, int y) : x(x),y(y) {};

    friend ostream& operator<< (ostream& os, const Point& point) {
        os << "(" << point.x << "," << point.y << ")";
        return os;
    }
};
class Player : public Point {
public:
    int id;
    int walls_left;

    Player(int id, int x, int y, int walls_left) {
        this->id = id;
        this->x = x;
        this->y = y;
        this->walls_left = walls_left;
    }

    void update(int x, int y, int walls_left) {
        this->x = x;
        this->y = y;
        this->walls_left = walls_left;
    }
};

class Wall : public Point {
public:
    string orientation;

    Wall(int x, int y, string orientation) {
        this->x = x;
        this->y = y;
        this->orientation = orientation;
    }
};

int width; // width of the board
int height; // height of the board
int n_players; // number of players (2 or 3)
int my_id; // id of my player (0 = 1st player, 1 = 2nd player, ...)

Point** my_goals;
Player** players;
Player* myPlayer;
char** walls;

void init() {
    // init seed
    fast_srand(3);

    // init walls
    walls = new char*[height];
    for (int i = 0; i < height; ++i)
        walls[i] = new char[width];
    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            walls[x][y] = 0;
        }
    }
    // init my_goals
    my_goals = new Point*[width];
    if(my_id == 0) {
        for(int i = 0; i < height; i++) {
            my_goals[i] = new Point(width-1,i);
        }
    } else if(my_id == 1) {
        for(int i = 0; i < height; i++) {
            my_goals[i] = new Point(0, i);
        }
    } else if(my_id == 2){
        for(int i = 0; i < width; i++) {
            my_goals[i] = new Point(width, height - 1);
        }
    }

    // init Players
    players = new Player*[n_players-1];
}

int main()
{
    cin >> width >> height >> n_players >> my_id; cin.ignore();

    init();

    int turn = 0;

    // game loop
    while (1) {
        for (int i = 0; i < n_players; i++) {
            int x; // x-coordinate of the player
            int y; // y-coordinate of the player
            int walls_left; // number of walls available for the player
            cin >> x >> y >> walls_left; cin.ignore();
            if(!turn) {
                // init players on first turn
                if(i != my_id) {
                    players[i] = new Player(i,x,y,walls_left);
                } else {
                    myPlayer = new Player(i,x,y,walls_left);
                }
            } else {
                // update players
                if(i != my_id) {
                    players[i]->update(x,y,walls_left);
                } else {
                    myPlayer->update(x,y,walls_left);
                }
            }
        }
        int wallCount; // number of walls on the board
        cin >> wallCount; cin.ignore();
        for (int i = 0; i < wallCount; i++) {
            int wallX; // x-coordinate of the wall
            int wallY; // y-coordinate of the wall
            string wallOrientation; // wall orientation ('H' or 'V')
            cin >> wallX >> wallY >> wallOrientation; cin.ignore();
            walls[wallX][wallY] = wallOrientation[0];
        }

        // Write an action using cout. DON'T FORGET THE "<< endl"
        // To debug: cerr << "Debug messages..." << endl;


        // action: LEFT, RIGHT, UP, DOWN or "putX putY putOrientation" to place a wall
        if(my_id == 0) {
                if(walls[myPlayer->x+1][myPlayer->y] == 'V' || walls[myPlayer->x+1][myPlayer->y-1] == 'V') {
                    if(fastRandInt(2))
                        cout << "UP" << endl;
                    else
                        cout << "DOWN" << endl;
                } else {
                    cout << "RIGHT" << endl;
                }
        } else if(my_id == 1){
            if(walls[myPlayer->x-1][myPlayer->y] == 'V' || walls[myPlayer->x-1][myPlayer->y-1] == 'V') {
                    if(fastRandInt(2))
                        cout << "UP" << endl;
                    else
                        cout << "DOWN" << endl;
            } else {
                cout << "LEFT" << endl;
            }
        } else {
            if(walls[myPlayer->x-1][myPlayer->y+1] == 'H' || walls[myPlayer->x][myPlayer->y+1] == 'H') {
                    if(fastRandInt(2))
                        cout << "LEFT" << endl;
                    else
                        cout << "RIGHT" << endl;
            } else {
                cout << "DOWN" << endl;
            }
        }
    }
}
