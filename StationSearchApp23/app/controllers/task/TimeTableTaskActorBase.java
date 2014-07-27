package controllers.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import play.libs.Time;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.*;


public class TimeTableTaskActorBase{
	
	private static TimeTableTaskActorBase instance = new TimeTableTaskActorBase();
	private ActorSystem system = ActorSystem.create("myActor");
	private ActorRef myTaskActor = system.actorOf(Props.create(TimeTableTaskActor.class));
	private Cancellable cancellable;
	
	private TimeTableTaskActorBase(){
		super();
	}
	
	public static TimeTableTaskActorBase getInstance(){
		return instance;
	}
	
	public void start() {
		try {
			Time.CronExpression ce = new Time.CronExpression("0 0 12 * * ?");
			Date nextValidTimeAfter = ce.getNextValidTimeAfter(new Date());
			FiniteDuration finiteDuration = 
					Duration.create(nextValidTimeAfter.getTime() - System.currentTimeMillis(),
							TimeUnit.MILLISECONDS);
			SimpleDateFormat D = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			System.out.println("時刻表取得タスク_次の実行時間：" + D.format(new Date(nextValidTimeAfter.getTime())));
			
			cancellable = system.scheduler().scheduleOnce(
					finiteDuration,
					myTaskActor,
					"Call",
					system.dispatcher(), null);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public void shutdown(){
		cancellable.cancel();
	}
}
