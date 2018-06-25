
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class HealingCafeV2 {
	static int income = 0;
	static class Log {
		public static void debug(String strMessage) {
			//���� ������ ��ȣ�� �Ű������� ���� Message ��� 
			System.out.println(Thread.currentThread().getName()  + " : " + strMessage);
		}
	}
	
	
	//---------------------------------------
	
	class MassageChair extends Semaphore {
		private static final long serialVersionUID = 1L;	//serializable ����ȭ ����� ���� ����
		
		public MassageChair(final int permits) {	//���ҽ��� ���� ����
			super(permits);
			System.out.println("�ȸ����� ��: " + permits + " ��");
		}
		
		public void use() throws InterruptedException {
			acquire(); 
			/* �������� ���ҽ� Ȯ��
			 * ���ҽ�(�ȸ�����)�� ���ڸ��� ������ ��� �ٷ� ������(�մ�)�� acquire �޼ҵ�κ��� ��ٷ� ���ƿ��� �ǰ� 
			 * �������� ���ο����� ���ҽ��� ���� �ϳ� ����. ���ҽ��� ���ڸ��� ���� ��� ������� acquire �޼ҵ� ���ο��� ���.
			 */
			try {
				doUse();	//�ȸ����� ���
			} finally {
				release();
				/* �������� ���ҽ� ����
				 * �������� ���ο����� ���ҽ�(�ȸ�����)�� �ϳ� ����. acquire �޼ҵ� �ȿ��� ������� ������(�մ�)�� ������
				 * �� �� �� ���� ��� acquire �޼ҵ�κ��� ���ƿ� �� �ִ�.
				 */
				Log.debug("���� �� ���� �ڸ� < " +   this.availablePermits() + " �� >");
			}
			System.out.println("\t\t\t\t\t\t\t\t�� ����: " + income + " ��");
		}
		protected void doUse() throws InterruptedException {
			// �մ��� �ȸ����ڸ� ����ϴ� �ð��� �������� ����	
			int usingTime = (int)(Math.random() * 20000)+1000;	//1��~20�ʱ��� ��� ����
			Thread.sleep(usingTime);	// �ȸ����ڸ� ����ϴ� �ð����� �����尡 ���ߵ��� ����
			int charge = (usingTime /1000) * 100;	//1�ʴ� 100
			income += charge;
			Log.debug("�ȸ����� ��� �ð�   ---  < " + usingTime/1000+"�� ���" +"(" + charge + "��) >");
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
		
		//10���� �ȸ����� ����
		MassageChair resource =  new HealingCafeV2().new MassageChair(10);
		//�� �� �Է�
		System.out.printf("�湮 ���� �� ���� �Է����ּ���: ");
		customers = scan.nextInt();
					
		if(customers==0)
		{
			System.out.println("���!!!\n���α׷��� ����˴ϴ�.");
		}
		for (int i = 0; i<customers ; i++) {		
			new HealingCafeV2().new Customers(i+1 + " ��", resource).start();
		}
	}
}