import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    // Constants for the game
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;

    // Arrays to hold the snake's x and y coordinates
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;          // Initial length of the snake
    private int applesEaten = 0;        // Score counter
    private int appleX;                 // X coordinate of the apple
    private int appleY;                 // Y coordinate of the apple
    private char direction = 'R';       // Snake's initial direction
    private boolean running = false;    // Game state
    private Timer timer;                // Controls the game loop speed
    private Random random;              // For random apple placement

    // Buttons for try again and exit
    private JButton tryAgainButton;
    private JButton exitButton;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();

        // Initialize buttons but set them invisible initially
        tryAgainButton = new JButton("Try Again");
        exitButton = new JButton("Exit");

        // Action listeners for buttons
        tryAgainButton.addActionListener(e -> restartGame());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to the panel but keep them invisible initially
        this.setLayout(null);
        tryAgainButton.setBounds(SCREEN_WIDTH / 2 - 75, SCREEN_HEIGHT / 2 + 50, 150, 40);
        exitButton.setBounds(SCREEN_WIDTH / 2 - 75, SCREEN_HEIGHT / 2 + 100, 150, 40);
        this.add(tryAgainButton);
        this.add(exitButton);

        // Hide buttons at the start of the game
        tryAgainButton.setVisible(false);
        exitButton.setVisible(false);
    }


    public void startGame() {
        // Set the snake's starting position to the center of the screen
        x[0] = SCREEN_WIDTH / 2;
        y[0] = SCREEN_HEIGHT;
        direction = 'U';  // Set initial direction to the right
        bodyParts = 10;    // Reset the snake's length
        applesEaten = 32;  // Reset the score
    
        newApple();       // Generate the first apple
        running = true;   // Start the game
        timer = new Timer(DELAY, this); // Set up the game loop
        timer.start();    // Start the timer
    }

    public void restartGame() {
        // Reset the snake's starting position to the center of the screen
        x[0] = SCREEN_WIDTH / 2;
        y[0] = SCREEN_HEIGHT / 2;
        direction = 'U';  // Set initial direction to the right
        bodyParts = 6;    // Reset the snake's length
        applesEaten = 0;  // Reset the score

        tryAgainButton.setVisible(false); // Hide buttons
        exitButton.setVisible(false);

        newApple();       // Generate a new apple
        running = true;   // Restart the game
        timer.start();    // Start the timer
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);               // Draw game elements
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // Draw the apple

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green); // Snake head color
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0)); // Snake body color
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // Draw the score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        // Randomly place a new apple on the screen
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        // Move each body part to the position of the previous one
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Move the head based on the direction
        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    public void checkApple() {
        // Check if the snake's head is on the apple
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;       // Increase the snake's length
            applesEaten++;     // Increase the score
            newApple();        // Generate a new apple
        }
    }

    public void checkCollisions() {
        // Check for self-collision
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // Check for wall collisions
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Display game over message
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Show buttons when game is over
        tryAgainButton.setVisible(true);
        exitButton.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Game loop method
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint(); // Redraw the game panel
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // Control the snake with arrow keys
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
            }
        }
    }
}
