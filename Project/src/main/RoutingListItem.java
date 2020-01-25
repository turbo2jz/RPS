package main;


/**
 * 路由表项类
 * @author always
 *
 */
public class RoutingListItem {

	/**
	 * 最大跳数
	 */
	public static final int maxHopNum=16;
	
	/**
	 * 目的网络(IP地址标识)
	 */
	private String destiNetwork;
	
	/**
	 * 下一跳路由器(RouterName标识)
	 */
	private String nextRouter;
	
	/**
	 * 跳数
	 */
	private int hopNum;
	
	/**
	 * 默认构造函数
	 */
	public RoutingListItem() {
		
	}
	
	/**
	 * 含参构造函数
	 * @param dNetwork 目的网络网络号
	 * @param nRouter 下一跳路由器名称
	 * @param hNum 跳数
	 */
	public RoutingListItem(String dNetwork,String nRouter,int hNum) {
		setDestiNetwork(dNetwork);
		setNextRouter(nRouter);
		setHopNum(hNum);
	}
	
	/**
	 * 复制构造函数
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
	 * 自增跳数
	 */
	public void increHopNum() {
		if(hopNum<maxHopNum) {
			hopNum++;
		}
	}

	/**
	 * 判断路由表项是否有相同目的网络
	 */
	public boolean equalsDNet(Object obj) {
		if(obj instanceof RoutingListItem) {
			return this.destiNetwork.equals(((RoutingListItem) obj).destiNetwork);
		}
		return false;
	}
	
	/**
	 * 判断路由表项是否有相同下一跳路由器
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
