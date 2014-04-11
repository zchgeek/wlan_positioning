package com.mc.collector;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * 包含了各种时间控制的函数
 * 使用时需要先实现抽象函数execute()，其内容是需要时间控制来执行的代码
 * @author Chao
 *
 */
abstract public class XTimer {
	
	Timer timer;
	int cnt;
	//*************************************************************//
	
	public XTimer(){
	}
	
	/**
	 * 循环执行程序times次，每个period毫秒执行一次，delay毫秒后开始第一次执行
	 * @param delay 
	 * @param period
	 * @param times
	 */
	public void Timer_DIT(long delay,long interval,int times){
		timer = new Timer();
		TimerTask task = task_DIT(times);
		timer.schedule(task, delay, interval);
	}
	
	public int progress(){
		return cnt;
	}
	
	private TimerTask task_DIT(final int times){
		TimerTask task = new TimerTask() {
			int counter = 0;
			@Override
			public void run() {
				cnt++;
				execute();
				if(++counter>=times) {
					timer.cancel();
					notice();
				}
			}
		};
		return task;
	}
	
	public void Timer_DI(long delay,long interval){
		timer = new Timer();
		TimerTask task = task_DI();
		timer.schedule(task, delay, interval);
	}
	
	private TimerTask task_DI(){
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				execute();
			}
		};
		return task;
	}
	
	public void cancel(){
		timer.cancel();
		timer.purge();
	}
	
	/**
	 * 抽象函数，具体执行任务需要开发者指定，在声明新MyTimer类时实现
	 */
	public abstract void execute();
	public abstract void notice();
}
