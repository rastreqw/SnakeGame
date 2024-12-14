import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

// Класс Snake для управления состоянием змейки
class Snake {
    public LinkedList<Point> body;
    private int directionX, directionY;
    private final int size;

    public Snake(int startX, int startY, int size) {
        this.size = size;
        body = new LinkedList<>();
        body.add(new Point(startX, startY)); // Начальная позиция
        directionX = 1; // Направление по оси X
        directionY = 0; // Направление по оси Y
    }

    // Двигаем змейку
    public void move(int width, int height) {
        Point newHead = getNewHead();
        checkBoundaryCollision(newHead, width, height);
        body.addFirst(newHead);
        body.removeLast();
    }

    // Устанавливаем направление змейки
    public void setDirection(int dx, int dy) {
        directionX = dx;
        directionY = dy;
    }

    // Проверяем столкновение с самой собой
    public boolean hasSelfCollision() {
        return body.stream().skip(1).anyMatch(segment -> segment.equals(body.getFirst()));
    }

    // Увеличиваем змейку
    public void grow() {
        body.addLast(new Point(body.getLast()));
    }

    // Рисуем змейку
    public void draw(Graphics g) {
        g.setColor(new Color(128, 0, 128));
        for (Point segment : body) {
            g.fillRect(segment.x, segment.y, size, size);
        }
    }

    private Point getNewHead() {
        Point head = body.getFirst();
        return new Point(head.x + directionX * size, head.y + directionY * size);
    }

    private void checkBoundaryCollision(Point newHead, int width, int height) {
        if (newHead.x < 0 || newHead.x >= width) {
            directionX = -directionX; // Отражаем по X
            newHead.setLocation(newHead.x + directionX * size, newHead.y);
        }
        if (newHead.y < 0 || newHead.y >= height) {
            directionY = -directionY; // Отражаем по Y
            newHead.setLocation(newHead.x, newHead.y + directionY * size);
        }
    }
}

// Класс Apple для управления состоянием яблока
class Apple {
    private Point position;
    private final int size;

    public Apple(int width, int height, int size) {
        this.size = size;
        generateNewApple(width, height);
    }

    // Рисуем яблоко
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(position.x, position.y, size, size);
    }

    // Проверяем, съела ли змейка яблоко
    public boolean isEatenBy(Snake snake) {
        return snake.body.getFirst().equals(position);
    }

    // Генерируем новое яблоко
    public void generateNewApple(int width, int height) {
        Random rand = new Random();
        position = new Point(rand.nextInt(width / size) * size, rand.nextInt(height / size) * size);
    }
}

// Класс GamePanel для отображения игры
class GamePanel extends JPanel {
    private Snake snake;
    private Apple apple;
    private final int width, height;
    private int speed; // Начальная скорость игры
    private boolean gameOver = false;

    public GamePanel(int width, int height) {
        this.width = width;
        this.height = height;
        this.speed = 100; // Скорость (мс)
        this.snake = new Snake(width / 2, height / 2, 20);
        this.apple = new Apple(width, height, 20);
        setupKeyListener();
    }

    private void setupKeyListener() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    handleKeyPress(e.getKeyCode());
                }
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W -> snake.setDirection(0, -1);
            case KeyEvent.VK_S -> snake.setDirection(0, 1);
            case KeyEvent.VK_A -> snake.setDirection(-1, 0);
            case KeyEvent.VK_D -> snake.setDirection(1, 0);
            case KeyEvent.VK_UP -> adjustSpeed(-40); // Увеличиваем скорость
            case KeyEvent.VK_DOWN -> adjustSpeed(40); // Уменьшаем скорость
        }
    }

    private void adjustSpeed(int delta) {
        speed = Math.max(10, Math.min(speed + delta, 200)); // Ограничиваем скорость
    }

    // Игровой цикл
    public void gameLoop() {
        if (!gameOver) {
            snake.move(width, height); // Двигаем змейку
            if (snake.hasSelfCollision()) {
                gameOver = true; // Завершение игры
            }
            if (apple.isEatenBy(snake)) {
                snake.grow();
                apple.generateNewApple(width, height);
            }
            repaint(); // Перерисовка
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameOver) {
            drawGameOver(g);
        } else {
            snake.draw(g);
            apple.draw(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(128, 0, 128));
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Game Over", width / 2 - 100, height / 2);
    }
}

// Класс SnakeGame для запуска игры
public class SnakeGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        GamePanel gamePanel = new GamePanel(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(gamePanel);
        frame.setVisible(true);

        // Игровой цикл
        while (true) {
            gamePanel.gameLoop();
            try {
                Thread.sleep(1000 / 15); // 15 кадров в секунду
            } catch (InterruptedException e) {
                e.printStackTrace(); // Обработка исключения
            }
        }
    }
}

