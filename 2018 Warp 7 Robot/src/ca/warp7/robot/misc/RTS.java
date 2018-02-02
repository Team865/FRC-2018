package ca.warp7.robot.misc;

import java.util.ArrayList;
import java.util.List;

public class RTS {
	private List<Runnable> tasks = new ArrayList<>();
	private double interpolation = 0;
	private int TICKS_PER_SECOND;
	private int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	private int MAX_FRAMESKIP;
	private boolean stoped = false;
	
	private Thread t;
	private String name=null;
	
	public RTS(int TICKS_PER_SECOND, int MAX_FRAMESKIP, String name){
		this.TICKS_PER_SECOND = TICKS_PER_SECOND;
		this.MAX_FRAMESKIP = MAX_FRAMESKIP;
		this.name = name;
	}
	
	public void start() {
		if (stoped){
			stoped = false;
			t = new Thread(() -> {
				double next_game_tick = System.currentTimeMillis();
			    int loops;
			    int a;
	
			    while (!stoped) {
			        loops = 0;
			        while (System.currentTimeMillis() > next_game_tick
			                && loops < MAX_FRAMESKIP) {
	
			        	for(Runnable task : tasks)
			                task.run();
	
			            next_game_tick += SKIP_TICKS;
			            loops++;
			        }
	
			        interpolation = (System.currentTimeMillis() + SKIP_TICKS - next_game_tick
			                / (double) SKIP_TICKS);
			        
			        a = (int) interpolation;
			        
			        try {
						Thread.sleep(a, (int) ((interpolation-a)*1000000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			    
		    });
		    t.start();
		}else{
			System.out.println("RTS is already running for object " + this.name);
		}
	}
	
	public void stop(){
		stoped = true;
	}
	
	public void addTask(Runnable task){
		tasks.add(task);
	}
}
