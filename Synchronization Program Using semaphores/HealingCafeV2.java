
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class HealingCafeV2 {
	static int income = 0;
	static class Log {
		public static void debug(String strMessage) {
			//현재 쓰레드 번호와 매개변수로 받은 Message 출력 
			System.out.println(Thread.currentThread().getName()  + " : " + strMessage);
		}
	}
	
	
	//---------------------------------------
	
	class MassageChair extends Semaphore {
		private static final long serialVersionUID = 1L;	//serializable 직렬화 사용을 위해 선언
		
		public MassageChair(final int permits) {	//리소스의 개수 설정
			super(permits);
			System.out.println("안마의자 수: " + permits + " 대");
		}
		
		public void use() throws InterruptedException {
			acquire(); 
			/* 세마포어 리소스 확보
			 * 리소스(안마의자)에 빈자리가 생겼을 경우 바로 쓰레드(손님)가 acquire 메소드로부터 곧바로 돌아오게 되고 
			 * 세마포어 내부에서는 리소스의 수가 하나 감소. 리소스에 빈자리가 없을 경우 쓰레드는 acquire 메소드 내부에서 블록.
			 */
			try {
				doUse();	//안마의자 사용
			} finally {
				release();
				/* 세마포어 리소스 해제
				 * 세마포어 내부에서는 리소스(안마의자)가 하나 증가. acquire 메소드 안에서 대기중인 쓰레드(손님)가 있으면
				 * 그 중 한 개가 깨어나 acquire 메소드로부터 돌아올 수 있다.
				 */
				Log.debug("종료 후 남은 자리 < " +   this.availablePermits() + " 석 >");
			}
			System.out.println("\t\t\t\t\t\t\t\t총 수익: " + income + " 원");
		}
		protected void doUse() throws InterruptedException {
			// 손님이 안마의자를 사용하는 시간을 랜덤으로 설정	
			int usingTime = (int)(Math.random() * 20000)+1000;	//1초~20초까지 사용 가능
			Thread.sleep(usingTime);	// 안마의자를 사용하는 시간동안 쓰레드가 멈추도록 설정
			int charge = (usingTime /1000) * 100;	//1초당 100
			income += charge;
			Log.debug("안마의자 사용 시간   ---  < " + usingTime/1000+"분 사용" +"(" + charge + "원) >");
		}
	}
	
	//------------------------------
	
	class Customers extends Thread {
		private final MassageChair resource;
		public Customers(String threadName, MassageChair resource) {
				this.setName(threadName);
				this.resource = resource;
		}
		@Override
		public void run() {
			try {
				resource.use();
			} catch (InterruptedException e) {
			} finally { }
		}
	}
	
	//-----------------------------------
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int customers ;
		
		//10대의 안마의자 생성
		MassageChair resource =  new HealingCafeV2().new MassageChair(10);
		//고객 수 입력
		System.out.printf("방문 고객의 명 수를 입력해주세요: ");
		customers = scan.nextInt();
					
		if(customers==0)
		{
			System.out.println("퇴근!!!\n프로그램이 종료됩니다.");
		}
		for (int i = 0; i<customers ; i++) {		
			new HealingCafeV2().new Customers(i+1 + " 님", resource).start();
		}
	}
}