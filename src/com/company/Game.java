package com.company;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.net.URL;
import javax.swing.*;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    static Hero hero = new Hero(Constants.SPAWN_FOR_HERO, Constants.minY, Constants.Vy_FOR_HERO,Constants.Vx_FOR_TEXTURE, Constants.Ay_FOR_HERO);
    Floor floor = new Floor(Constants.SPAWNx_FOR_FLOOR,Constants.SPAWNy_FOR_FLOOR);
    public Queue <Block> blockQueue = new PriorityQueue<Block>();
    Random RG = new Random();
    private boolean running;
    private boolean gameOver;
    private Sprite backgroundImg  = getSprite("pictures/Background.png");
    static String NAME = "First Stage";
    int pos;
    private boolean upPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    static boolean MaxSpeed = false;
    private boolean CHECK_THE_RESTART = false;
    static boolean CHECK_THE_JUMP = false;
    static int Check_The_Speed = 0;
    double Block_Speed = Constants.Vx_FOR_TEXTURE;

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public void run(){
        long delta;
        long lastTime = System.currentTimeMillis();
        init();

        while (running) {
            delta = (System.currentTimeMillis() - lastTime);
            lastTime = System.currentTimeMillis();
            render();
            update((double) delta / Constants.deltaConst);

            /* if ((Check_The_Speed >= 10) && (!MaxSpeed)) {
                Block_Speed+=Constants.Ax_FOR_BLOCK;
                if (Block_Speed >= Constants.MAX_SPEED_FOR_BLOCK) {
                    MaxSpeed = true;
                }
                Check_The_Speed =0;
            }*/

            if(gameOver) {
                try{
                    TimeUnit.SECONDS.sleep(2);
                }catch(Exception e){
                    System.out.print(e);
                }
                CHECK_THE_RESTART = true;
            }
            System.out.print(CHECK_THE_RESTART+" ");
        }
    }

    private void restart(){
        hero = new Hero(Constants.SPAWN_FOR_HERO, Constants.minY, Constants.Vy_FOR_HERO,Constants.Vx_FOR_TEXTURE,Constants.Ay_FOR_HERO);
        Floor floor = new Floor(Constants.SPAWNx_FOR_FLOOR,Constants.SPAWNy_FOR_FLOOR);
        Queue <Block> blockQueue = new PriorityQueue<Block>();
        Block_Speed = Constants.Vx_FOR_TEXTURE;
    }

    public void init() {

        addKeyListener(new KeyInputHandler());
        java.util.Timer timer_for_hero = new java.util.Timer();
        java.util.Timer timer_for_block = new java.util.Timer();
        timer_for_hero.schedule(new TimerTask() {
            @Override
            public void run() {
                if (pos++ > 5) {
                    pos = 1;
                }
                if (!Game.CHECK_THE_JUMP) {
                    Game.hero.image = Game.getSprite("pictures/sprites_8.run" + pos + ".png");
                }
            }
        }, Constants.DIPLAY, Constants.PERIOD);

        timer_for_block.schedule(new TimerTask() {
            @Override
            public void run() {
                blockQueue.add(new Block((int)Constants.SPAWN_FOR_BLOCK, Constants.SPAWNy_FOR_BLOCK, Block_Speed,RG.nextInt(2)));
            }
        }, 0, 3000);
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            requestFocus();
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.black);
        g.setColor(Color.black);
        g.fillRect(0,0, getWidth(), getHeight());
        backgroundImg.draw(g,0,0);

        floor.image.draw(g,floor.getX(),floor.getY());

        if(hero != null){
                hero.image.draw(g, hero.getX(), hero.getY());
        }

        for(Block block : blockQueue)
            block.image.draw(g, block.getX(), block.getY());
        g.dispose();
        bs.show();

    }

    public void update(double delta) {

        hero.jump(delta);

        if (leftPressed) {
            Hero.runningLeft(delta);
        }

        if (rightPressed) {
            Hero.runningRight(delta);
        }

        if(blockQueue != null && !blockQueue.isEmpty() && blockQueue.peek().getX() < -500 )
                blockQueue.poll();
                if(blockQueue != null && !blockQueue.isEmpty()) {
                    for(Block block : blockQueue){
                if(block.overlaps(hero)) gameOver = true;
            }
        }
        for(Block block : blockQueue) block.BlockMoving(delta);

    }

    public static Sprite getSprite(String path) {
        URL url = Game.class.getResource(path);
        Image sourceImage = new ImageIcon(url).getImage();
        Sprite sprite = new Sprite(sourceImage);
        return sprite;
    }


    private class KeyInputHandler extends KeyAdapter {

        public void keyPressed(KeyEvent e) {

            if ((e.getKeyCode() == KeyEvent.VK_UP) && (hero.getY() >= Constants.minY - 1)) {
                upPressed = true;
            }

            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            }

            if (((e.getKeyCode() == KeyEvent.VK_SPACE)) && (CHECK_THE_RESTART)) {
                gameOver = false;
                CHECK_THE_RESTART = false;
                restart();
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (upPressed)
                    Hero.processUpPressed();
                upPressed = false;
            }

            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = false;
            }

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            }
        }
    }
}