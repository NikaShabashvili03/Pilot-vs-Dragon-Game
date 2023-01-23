import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.awt.Toolkit;
import java.util.Random;
public class Main extends JFrame{
    private static Image Sky;
    private static Image restart;
    private static Main game_window;
    private static Image Airplane;
    private static Image Bullet;
    private static Image Enemy;
    private static Image Boom;
    private static Image spacebar;
    private static Image arrows;
    private static boolean gameOver = false;
    private static boolean gameWin = false;
    private static boolean startGame = false;

    private static int height = 530;
    private static int y = 200;

    private static int x = 0;

    private static float enemyX = 1000.0f;
    private static int enemyY;

    private static int speed = 100;
    private static int bulletX = x+20;

    private static boolean openFire = false;
    private static int count = 0;

    private static float restart_x = 1000/2 - 50;
    private static float restart_y = 600/2;

    public static void main(String[] args) throws IOException {
        Airplane = ImageIO.read(Main.class.getResourceAsStream("Images/Airplane.png"));
        Bullet = ImageIO.read(Main.class.getResourceAsStream("Images/bullet.png"));
        Enemy = ImageIO.read(Main.class.getResourceAsStream("Images/enemy.png"));
        Boom = ImageIO.read(Main.class.getResourceAsStream("Images/boom.png"));
        Sky = ImageIO.read(Main.class.getResourceAsStream("Images/Sky.gif"));
        restart = ImageIO.read(Main.class.getResourceAsStream("Images/reload.png"));
        arrows = ImageIO.read(Main.class.getResourceAsStream("Images/arrows.png"));
        spacebar = ImageIO.read(Main.class.getResourceAsStream("Images/spacebar.png"));
        game_window = new Main();
        game_window.setSize(1000,height);
        game_window.setResizable(false);
        game_window.setTitle("Pilot vs Dragon");
        game_window.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                float restart_x_left = restart_x + restart.getWidth(null);
                float restart_y_bottom = restart_y + restart.getHeight(null);
                if(gameOver || gameWin){
                    if(mx >= restart_x && mx <= restart_x_left && my >= restart_y && my <= restart_y_bottom){
                        x = 0;
                        y = 200;
                        gameOver = false;
                        gameWin = false;
                        bulletX = x+20;
                        enemyX = 1000.0f;
                        count = 0;
                        startGame = false;
                        openFire = false;
                    }
                }
            }
        });
        game_window.setDefaultCloseOperation(EXIT_ON_CLOSE);
        game_window.setLocation(200,150);
        GameField gameF = new GameField();
        game_window.add(gameF);
        Random rand = new Random();
        enemyY = 100 * rand.nextInt(5);
        game_window.setVisible(true);
        game_window.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if(!gameOver && !gameWin){
                    if (keyCode == KeyEvent.VK_UP) {
                        moveUp();
                        startGame = true;
                    }
                    else if (keyCode == KeyEvent.VK_DOWN) {
                        moveDown();
                        startGame = true;
                    }
                    else if (keyCode == KeyEvent.VK_LEFT) {
                        moveLeft();
                        startGame = true;
                    }
                    else if (keyCode == KeyEvent.VK_RIGHT) {
                        moveRight();
                        startGame = true;
                    }
                    else if (keyCode == KeyEvent.VK_SPACE) {
                        if(startGame){
                            if(!openFire){
                                startFire();
                            }
                        }
                    }
                }
            }
        });
    }
    public static void onRepaint (Graphics g){
        g.drawImage(Sky, 0, 0, 1000, 500, null);
        if(!gameOver && !gameWin){
            g.setColor(Color.white);
            g.drawString("Count: " + count + " / " + "10",10,20);
            g.drawImage(Airplane, x, y,100,100, null);
            if (openFire){
                g.drawImage(Bullet, bulletX, y,50,50,null);
            }
            g.drawImage(Enemy, (int)enemyX, enemyY, 100, 100, null);
        }
        if(gameOver){
            g.drawImage(Boom, x-100, y-100, 300, 300, null);
            g.setColor(new Color(100,100,100,200));
            g.fillRect(0, 0, 1000, 500);
            g.setColor(Color.black);
            g.setFont(g.getFont().deriveFont(45.0f));
            g.drawString("You lose game", 1000/2 - 147,500/2);
            g.drawImage(restart, (int)restart_x,(int) restart_y, 100,100,null);
        }
        if(gameWin){
            g.setColor(Color.black);
            g.setFont(g.getFont().deriveFont(35.0f));
            g.drawString("You win game", 1000/2 - 147,500/2);
            g.drawImage(restart, (int)restart_x,(int) restart_y, 100,100,null);
        }
        if(!startGame){
            g.setColor(new Color(100,100,100,200));
            g.fillRect(0, 0, 1000, 500);
            g.setColor(Color.black);
            g.setFont(g.getFont().deriveFont(35.0f));
            g.drawString("Press This Buttons For Movement ---> ", 1000/2 - 440,500/2);
            g.drawImage(arrows, 1000/2 + 250, 150, 150, 150, null);
            g.setColor(Color.black);
            g.setFont(g.getFont().deriveFont(25.0f));
            g.drawString("Shoot ---> ", 700,480);
            g.drawImage(spacebar, 840, 450, 150, 50, null);
        }
    }
    private static class GameField extends JPanel{
        @Override
        protected void paintComponent (Graphics g){
            super.paintComponent(g);
            onRepaint(g);
            g.setColor(Color.RED);
            if(!gameOver && !gameWin && startGame){
                enemyX = enemyX - 0.3f;
                if(enemyX < 0){
                    enemyX = 1000;
                    Random rand = new Random();
                    enemyY = 100 * rand.nextInt(5);
                }
                if ((int)enemyX == x && y == enemyY) {
                    gameOver = true;
                }
                if(openFire){
                    bulletX = bulletX + 1;
                    if(bulletX >= 900){
                        bulletX = x+20;
                        openFire = false;
                    } else if (bulletX > enemyX && y == enemyY) {
                        bulletX = x + 20;
                        openFire = false;
                        count += 1;
                        enemyX = 1000;
                        Random rand = new Random();
                        enemyY = 100 * rand.nextInt(5);
                        if(count == 10){
                            gameWin = true;
                        }
                    }
                }
            }
            repaint();
        }
    }

    public static void moveUp(){
        if(y > 0){
            y -= speed;
        }
    }
    public static void moveDown(){
        if(y < height - 200){
            y += speed;
        }
    }
    public static void moveLeft(){
        if(x > 0){
            x -= speed;
        }
    }
    public static void moveRight(){
        if(x < 300){
            x += speed;
        }
    }
    public static void startFire(){
        openFire = true;
    }
}