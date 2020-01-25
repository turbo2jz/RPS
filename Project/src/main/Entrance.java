package main;
import java.util.List;
import java.util.ArrayList;

import common.IPAddress;
import common.IPType;

/**
 * 入口类
 * 仅做控制台初步测试，实际项目入口见MainFrame
 * @author always
 *
 */
public class Entrance {
	
	/**
	 * 仅做控制台初步测试，实际项目入口见MainFrame
	 * @param args
	 */
	public static void main(String[] args) {
		
		//测试网络拓扑结构：
		//|192.168.1.0|<->|Router1|<->|192.168.2.0|<->|Router2|<->|192.168.3.0|<->|Router3|<->|192.168.4.0|
		List<Router> routerList=new ArrayList<Router>();
		List<Network> networkList=new ArrayList<Network>();
		for(int i=1;i<=3;i++) {
			Router t=new Router("Router"+i);
			t.setName("Thread_Router"+i);
			routerList.add(t);
		}
		for(int i=1;i<=4;i++) {
			Network n=new Network(new IPAddress(192,168,i,0,IPType.netid));
			n.setName("192.168."+i+".0");
			networkList.add(n);
		}
		List<String> temp=new ArrayList<String>();
		List<String> temp1=new ArrayList<String>();
		
		
		temp.add(networkList.get(0).getnetID().toString());
		temp.add(networkList.get(1).getnetID().toString());
		routerList.get(0).setdestiNetworkList(temp);
		temp.clear();

		temp.add(networkList.get(1).getnetID().toString());
		temp.add(networkList.get(2).getnetID().toString());
		routerList.get(1).setdestiNetworkList(temp);
		temp.clear();
		
		temp.add(networkList.get(2).getnetID().toString());
		temp.add(networkList.get(3).getnetID().toString());
		routerList.get(2).setdestiNetworkList(temp);
		temp.clear();
		
		temp1.add(routerList.get(0).getRouterName());
		networkList.get(0).setDestiRouterList(temp1);
		temp1.clear();

		temp1.add(routerList.get(0).getRouterName());
		temp1.add(routerList.get(1).getRouterName());
		networkList.get(1).setDestiRouterList(temp1);
		temp1.clear();

		temp1.add(routerList.get(1).getRouterName());
		temp1.add(routerList.get(2).getRouterName());
		networkList.get(2).setDestiRouterList(temp1);
		temp1.clear();
		
		temp1.add(routerList.get(2).getRouterName());
		networkList.get(3).setDestiRouterList(temp1);
		temp1.clear();
		
		for(Router r:routerList) {
			r.start();
		}
		
		for(Network n:networkList) {
			n.start();
		}
		
	}

}
