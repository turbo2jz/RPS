package common;

/**
 * IP��ַ��
 * @author always
 */
public class IPAddress {
	
	private int sub1,sub2,sub3,sub4;
	
	private String IP;
	
	private IPType type;

	/**
	 * ���ι��캯��
	 * ͨ��IP��ַ�ַ������г�ʼ�������ܻ����Խ��
	 * @param IPstr
	 * @param t
	 */
	public IPAddress(String IPstr,IPType t) {
		String[] subArr=(IPstr.trim()).split("\\.");
		int[] subIntArr=new int[subArr.length];
		for(int i=0;i<subArr.length;i++) {
			subIntArr[i]=Integer.parseInt(subArr[i]);
		}
		sub1=subIntArr[0];
		sub2=subIntArr[1];
		sub3=subIntArr[2];
		sub4=subIntArr[3];
		IP=IPstr;
		type=t;
	}
	
	/**
	 * ���ι��캯��
	 * ͨ����IP��ַÿһλ���г�ʼ��
	 * @param s1
	 * @param s2
	 * @param s3
	 * @param s4
	 * @param t
	 */
	public IPAddress(int s1,int s2,int s3,int s4,IPType t) {
		sub1=s1;
		sub2=s2;
		sub3=s3;
		sub4=s4;
		type=t;
		IP=""+sub1+'.'+sub2+'.'+sub3+'.'+sub4;
	}
	
	/**
	 * ���ƹ��캯��
	 */
	public IPAddress(IPAddress copyIP) {
		sub1=copyIP.sub1;
		sub2=copyIP.sub2;
		sub3=copyIP.sub3;
		sub4=copyIP.sub4;
		type=copyIP.type;
		IP=new String(copyIP.IP);
	}
	
	/**
	 * ����IP��ַ����
	 * @return
	 */
	public IPType getIpType() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IPAddress) {
			return this.IP.equals(((IPAddress) obj).IP);
		}
		return false;
	}

	@Override
	public String toString() {
		return ""+sub1+'.'+sub2+'.'+sub3+'.'+sub4;
	}

}
