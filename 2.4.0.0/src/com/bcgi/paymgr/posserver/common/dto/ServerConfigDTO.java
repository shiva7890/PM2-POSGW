package com.bcgi.paymgr.posserver.common.dto;

public class ServerConfigDTO {

	private  int serverPort = 0;
    private  String isoMsgLogFileName 	 = null;
    private  String threadlogFileName 	 = null;
    private  String isoMessageHeader  	 = null;
    private  String msgPackagerFormat 	 = null;
    private  int messageFormat 	  	     = 0;
    private  String packagerFile 	  	 = null;
    private  int 	threadPoolMinSize 	 = 0;
    private  int 	threadPoolMaxSize 	 = 0;
    private  int 	workerThreadPoolMinSize 	 = 0;
    private  int 	workerThreadPoolMaxSize 	 = 0;
    private  int 	lengthOfMobileNumber = 0;
    private  int 	nodeCount			 = 0;
    private int systemMonitorFrequency = 0;
    //The Following code is added by Sridhar.vemulapalli on 12-Aug-2011
    private String useOldNACChannel =null;

private int appThreadPoolSize = 0;

    //Added by Sandeep Gudimetla on 27-FEB-2013
    private String connTimeOut = null;
    private String connReadTimeOut = null;
    
	public String getUseOldNACChannel() {
		return useOldNACChannel;
	}

	public void setUseOldNACChannel(String useOldNACChannel) {
		this.useOldNACChannel = useOldNACChannel;
	}
	//Ends

	public ServerConfigDTO() {

	}

	public void setPort(int port){
		serverPort = port;
	}

	public int getPort(){
		return serverPort;
	}

	public void setIsoMsgLogFileName(String fileName){
		isoMsgLogFileName = fileName;
	}

	public String getIsoMsgLogFileName(){
		return isoMsgLogFileName;
	}

	public void setThreadlogFileName(String fileName){
		threadlogFileName = fileName;
	}

	public String getThreadlogFileName(){
		return threadlogFileName;
	}

	public void setISOMessageHeader(String header){
		isoMessageHeader = header;
	}

	public String getISOMessageHeader(){
		return isoMessageHeader;
	}

	public void setMsgPackagerFormat(String pckgerFormat){
		msgPackagerFormat = pckgerFormat;
	}

	public String getMsgPackagerFormat(){
		return msgPackagerFormat;
	}

	public void setMessageFormat(int msgFormat){
		messageFormat = msgFormat;
	}

	public int getMessageFormat(){
		return messageFormat;
	}
	
	public void setPackagerFile(String pckgerFile){
		packagerFile = pckgerFile;
	}

	public String getPackagerFile(){
		return packagerFile;
	}


	public void setThreadPoolMinSize(int thrdPoolMinSize)
	{
		threadPoolMinSize = thrdPoolMinSize;
	}

	public int getThreadPoolMinSize()
	{
		return threadPoolMinSize;
	}

	public void setThreadPoolMaxSize(int thrdPoolMaxSize)
	{
		threadPoolMaxSize = thrdPoolMaxSize;
	}

	public int getThreadPoolMaxSize()
	{
		return threadPoolMaxSize;
	}

	public void setLengthOfMobileNumber(int lngthOfMobileNumber)
	{
		lengthOfMobileNumber = lngthOfMobileNumber;
	}

	public int getLengthOfMobileNumber()
	{
		return lengthOfMobileNumber;
	}

	public void setNodeCount(int nodeCount)
	{
		this.nodeCount = nodeCount;
	}

	public int getNodeCount()
	{
		return nodeCount;
	}

	public int getWorkerThreadPoolMinSize() {
		return workerThreadPoolMinSize;
	}

	public void setWorkerThreadPoolMinSize(int workerThreadPoolMinSize) {
		this.workerThreadPoolMinSize = workerThreadPoolMinSize;
	}

	public int getWorkerThreadPoolMaxSize() {
		return workerThreadPoolMaxSize;
	}

	public void setWorkerThreadPoolMaxSize(int workerThreadPoolMaxSize) {
		this.workerThreadPoolMaxSize = workerThreadPoolMaxSize;
	}

	public int getSystemMonitorFrequency() {
		return systemMonitorFrequency;
	}

	public void setSystemMonitorFrequency(int systemMonitorFrequency) {
		this.systemMonitorFrequency = systemMonitorFrequency;
	}
	
	public int getAppThreadPoolSize() {
		return appThreadPoolSize;
	}

	public void setAppThreadPoolSize(int appThreadPoolSize) {
		this.appThreadPoolSize = appThreadPoolSize;
	}

    public String getConnTimeOut() {
		return connTimeOut;
	}

	public void setConnTimeOut(String connTimeOut) {
		this.connTimeOut = connTimeOut;
	}

	public String getConnReadTimeOut() {
		return connReadTimeOut;
	}

	public void setConnReadTimeOut(String connReadTimeOut) {
		this.connReadTimeOut = connReadTimeOut;
	}

	

}
