package main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import common.IPAddress;
import ui.NetworkPanel;

/**
 * 网络类
 * @author always
 *
 */
public class Network extends Thread {

	/**
	 * 网络集合Map(静态)
	 * ((String)Network.netID,Network)
	 * 注：用于路由器通过网络号查找网络对象，网络对象创建即被添加至该集合
	 */
	public static Map<String,Network> NetworkList;
	
	/**
	 * 休眠时间
	 * 注：网络发送RIP报文集合的时间间隔
	 */
	private static final long SleepTime=(long) 1000;
	
	/**
	 * 网络号
	 */
	private IPAddress netID;
	
	/**
	 * 直连路由器列表
	 * 列表内容：(Router.Name)
	 */
	private List<String> destiRouterList;
	
	/**
	 * 直连路由器状态集合
	 * 列表内容：(Router.Name,是否正常连接)
	 */
	private Map<String,Boolean> destiRouterMap;
	
	/**
	 * RIP报文缓冲区
	 * 集合内容：(Router.Name,Router.routingList)
	 */
	private Map<String,List<RoutingListItem>> RIPCache;
	
	/**
	 * 路由器信息展示面板
	 */
	private NetworkPanel showPanel;
	
	//网络集合初始化
	static {
		NetworkList=new HashMap<String,Network>();
	}
	
	/**
	 * 添加网络至网络集合Map
	 * @param net
	 */
	public static void addNetwork(Network net) {
		NetworkList.put(net.netID.toString(), net);
	}
	
	/**
	 * 默认构造函数
	 */
	public Network() {
		
	}
	
	/**
	 * 含参构造函数
	 * @param nID 网络号
	 */
	public Network(IPAddress nID) {
		netID=nID;
		infoListInit();
		addNetwork(this);
	}
	
	/**
	 * 信息列表初始化
	 */
	private void infoListInit() {
		//直连路由器队列初始化
		destiRouterList=new CopyOnWriteArrayList<String>();
		//直连路由器状态集合初始化
		destiRouterMap=new ConcurrentHashMap<String,Boolean>();
		//RIP报文缓存队列初始化
		RIPCache=new ConcurrentHashMap<String,List<RoutingListItem>>();
	}
	
	/**
	 * 获取网络网络号
	 * @return netID 网络号
	 */
	public IPAddress getnetID() {
		return netID;
	}
	
	/**
	 * 获取直连路由器列表
	 * @return destiRouterList 直连路由器列表
	 */
	public List<String> getDestiRouterList(){
		return destiRouterList;
	}
	
	/**
	 * 获取直连路由器状态
	 * @return destiRouterMap
	 */
	public Map<String,Boolean> getDestiRouterMap(){
		return destiRouterMap;
	}
	
	/**
	 * 设置网络网络号
	 * @param nID 网络号
	 */
	public void setnetID(IPAddress nID) {
		netID=nID;
	}
	
	/**
	 * 设置直连路由器列表
	 * 注：此方法在路由器线程运行前调用，用以设置直连路由器列表
	 * @param dRouterList 直连路由器列表
	 */
	public void setDestiRouterList(List<String> dRouterList) {
		destiRouterList.addAll(dRouterList);
		for(String drouter:destiRouterList) {
			destiRouterMap.put(drouter,true);
		}
	}
	
	/**
	 * 设置单个直连路由器
	 * 注： 此方法会在网络对象运行前后均有可能由路由器进行调用，因此要进行并发控制
	 * @param routerName 路由器名
	 */
	public synchronized void setOneDestiRouter(String routerName) {
		destiRouterList.add(routerName);
		destiRouterMap.put(routerName,true);
	}
	
	/**
	 * 设置路由器信息显示面板
	 * @param np 显示面板
	 */
	public void setShowPanel(NetworkPanel np) {
		showPanel=np;
	}
	
	/**
	 * 设置直连路由器状态
	 * 注：由路由器调用
	 * @param routerName 路由器名
	 * @param flag 是否正常连接
	 * @return Boolean 是否成功设置
	 */
	public synchronized Boolean setRouterIsReachable(String routerName, Boolean flag) {
		if(destiRouterList.contains(routerName)) {
			//设置网络的路由器状态集合中的该路由器为不可达状态
			destiRouterMap.replace(routerName, flag);
			//设置该路由器的网络状态集合的此网络为不可达状态
			Router.RouterList.get(routerName).setNetIsReachableForNet(netID.toString(), flag);
			return true;
		}
		return false;
	}
	
	/**
	 * 设置当前网络是否与路由器相连
	 * 注：由路由器调用
	 * @param routerName
	 * @param flag
	 * @return
	 */
	public Boolean setRouterIsReachableForRouter(String routerName,Boolean flag) {
		if(destiRouterMap.containsKey(routerName)) {
			destiRouterMap.replace(routerName, flag);
			return true;
		}
		return false;
	}
	
	/**
	 * 添加从路由器获取的RIP报文至RIPCache
	 * 注：此方法由路由器对象调用，会出现并发访问this.RIPCache，需进行并发访问控制
	 * @param sentRouter 发送路由器名
	 * @param rList 路由表
	 */
	public synchronized void putRIPCache(String sentRouter,List<RoutingListItem> rList) {
		//若Cache存在该路由器发来的RIP报文，移除原报文
		if(RIPCache.containsKey(sentRouter)) {
			RIPCache.remove(sentRouter);
		}
		//报文添加
		RIPCache.put(sentRouter, rList);
	}
	
	/**
	 * 获取专属于某一路由器的RIP报文集合
	 * 注：去除由该路由器自己发送的报文，此处需获取RIPCache，需进行并发访问控制
	 * @param dRouterName 路由器名
	 * @return Map< String,List< RoutingListItem > > 专属RIP报文集合
	 */
	private synchronized Map<String,List<RoutingListItem>> getDedicRIPCache(String dRouterName) {
		//临时变量result
		Map<String,List<RoutingListItem>> result=new ConcurrentHashMap<String,List<RoutingListItem>>();
		result.putAll(RIPCache);
		//移除由路由器dRouterName发送的报文
		result.remove(dRouterName);
		return result;
	}
	
	/**
	 * 发送获取RIP报文集合至邻接路由器RIPCache
	 * 注：此方法调用路由器类的putRIPCache方法，将本网络缓存的IP报文发送至本
	 * 网络邻接的路由器
	 */
	private void sentSelfRIP() { 
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.infoShow.append(df.format(new Date())+"\n");
		for(String dRouterName:destiRouterList) {
			if(destiRouterMap.get(dRouterName)) {
				Router router=Router.RouterList.get(dRouterName);
				router.putRIPCache(this.getDedicRIPCache(router.getRouterName()));
				showPanel.infoShow.append(netID.toString()+" --> "+dRouterName+"\n");
			}
			else {
				showPanel.infoShow.append(netID.toString()+" --X "+dRouterName+"\n");
			}
		}
		showPanel.infoShow.append("\n");
	}
	
	/**
	 * 在面板显示信息
	 */
	private synchronized void showInfo() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.timeLabel.setText(df.format(new Date()));
		DefaultListModel<String> model=new DefaultListModel<String>();
		for(String rname:destiRouterList) {
			model.addElement(rname);
		}
		showPanel.destiRouterList.setModel(model);
		StringBuilder sb=new StringBuilder();
		for(Map.Entry<String,List<RoutingListItem>> entry : RIPCache.entrySet()) {
			sb.append(entry.getKey()+"_RIP报文：\n");
			List<RoutingListItem> tList=entry.getValue();
			sb.append(String.format("%s\t%s\t%s\n","目的网络","下一跳路由器","跳数"));
			for(RoutingListItem item:tList) {
				sb.append(item.toString()+"\n");
			}
		}
		showPanel.routingShow.setText(sb.toString());
		showPanel.updateUI();
	}
	
	@Override
	public void run() {
		while(true) {
			//输出显示
			showInfo();
			//发送RIP报文缓存至路由器
			sentSelfRIP();
			try {
				Thread.sleep(SleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
