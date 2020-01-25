package main;


/**
 * ·�ɱ�����
 * @author always
 *
 */
public class RoutingListItem {

	/**
	 * �������
	 */
	public static final int maxHopNum=16;
	
	/**
	 * Ŀ������(IP��ַ��ʶ)
	 */
	private String destiNetwork;
	
	/**
	 * ��һ��·����(RouterName��ʶ)
	 */
	private String nextRouter;
	
	/**
	 * ����
	 */
	private int hopNum;
	
	/**
	 * Ĭ�Ϲ��캯��
	 */
	public RoutingListItem() {
		
	}
	
	/**
	 * ���ι��캯��
	 * @param dNetwork Ŀ�����������
	 * @param nRouter ��һ��·��������
	 * @param hNum ����
	 */
	public RoutingListItem(String dNetwork,String nRouter,int hNum) {
		setDestiNetwork(dNetwork);
		setNextRouter(nRouter);
		setHopNum(hNum);
	}
	
	/**
	 * ���ƹ��캯��
	 */
	public RoutingListItem(RoutingListItem copyItem) {
		setDestiNetwork(new String(copyItem.destiNetwork));
		setNextRouter(new String(copyItem.nextRouter));
		setHopNum(copyItem.hopNum);
	}
	
	public String getDestiNetwork() {
		return destiNetwork;
	}
	
	public void setDestiNetwork(String dNetwork) {
		destiNetwork=dNetwork;
	}
	
	public String getNextRouter() {
		return nextRouter;
	}
	
	public void setNextRouter(String nRouter) {
		nextRouter=nRouter;
	}
	
	public int getHopNum() {
		return hopNum;
	}
	
	public void setHopNum(int hNum) {
		hopNum=hNum;
	}
	
	/**
	 * ��������
	 */
	public void increHopNum() {
		if(hopNum<maxHopNum) {
			hopNum++;
		}
	}

	/**
	 * �ж�·�ɱ����Ƿ�����ͬĿ������
	 */
	public boolean equalsDNet(Object obj) {
		if(obj instanceof RoutingListItem) {
			return this.destiNetwork.equals(((RoutingListItem) obj).destiNetwork);
		}
		return false;
	}
	
	/**
	 * �ж�·�ɱ����Ƿ�����ͬ��һ��·����
	 */
	public boolean equalsNRouter(Object obj) {
		if(obj instanceof RoutingListItem) {
			return this.nextRouter.equals(((RoutingListItem) obj).getNextRouter());
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%15s%12s%5d",destiNetwork,nextRouter,hopNum);
	}
	
}
