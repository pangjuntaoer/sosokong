package cn.edu.swust.processor.fetch.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import cn.edu.swust.utils.StaticParameters;

/**
 * 自定义参数的Httpclient。<br>
 * 提供httpGet，httpPost两种传送消息的方式<br>
 * 提供httpPost上传文件的方式
 */
public class QHttpClient {
	private static String COOKIE = "_hc.v='\"10293932-04f4-4798-982b-9fa3468f702f.1406359230\"';"
			+ "abtest='48,124\\|52,133\\|47,122\\|44,106\\|45,115'; tc=8; PHOENIX_ID=0a010444-148ab7d9962-2989d5; s_ViewType=10; JSESSIONID=0EAEDF27D96560BB8DEF6ED5DBDADFA5; aburl=1; cy=8; cye=chengdu; __utma=1.871512914.1411199946.1411559439.1411626401.5; __utmb=1.19.10.1411626401; __utmc=1; __utmz=1.1411626401.5.3.utmcsr=baidu|utmccn=(organic)|utmcmd=organic|utmctr=%E5%A4%A7%E4%BC%97%E7%82%B9%E8%AF%84%E7%BD%91";

	// SDK默认参数设置
	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int CON_TIME_OUT_MS = 10000;
	public static final int SO_TIME_OUT_MS = 10000;
	public static final int MAX_CONNECTIONS_PER_HOST = 20;
	public static final int MAX_TOTAL_CONNECTIONS = 200;
	//去掉url锚文本标记 #*** 或 #×××;
	public static final String ANCHOR="#\\w*;?";
	// 日志输出
	private static Log log = LogFactory.getLog(QHttpClient.class);
	
	private DefaultHttpClient httpClient;
	private HttpContext httpContext;
	private int httpResponseCode = 0;

	public QHttpClient() {
		this(MAX_CONNECTIONS_PER_HOST, MAX_TOTAL_CONNECTIONS, CON_TIME_OUT_MS,
				SO_TIME_OUT_MS, null, null);
	}

	public void initialCookie(CookieStore cookieStore) {
		httpClient.setCookieStore(cookieStore);
	}

	public void destroyCustomCookie() {
		httpClient.setCookieStore(null);
	}
	/**
	 * 去掉url中锚文本标记
	 * @param url
	 * @return
	 */
	public static String formatURI(String url){
		String formatURI=url.replaceAll(QHttpClient.ANCHOR, "");
		return formatURI;
	}
	/**
	 * 个性化配置连接管理器
	 * 
	 * @param maxConnectionsPerHost
	 *            设置默认的连接到每个主机的最大连接数
	 * @param maxTotalConnections
	 *            设置整个管理连接器的最大连接数
	 * @param conTimeOutMs
	 *            连接超时
	 * @param soTimeOutMs
	 *            socket超时
	 * @param routeCfgList
	 *            特殊路由配置列表，若无请填null
	 * @param proxy
	 *            代理设置，若无请填null
	 */
	public QHttpClient(int maxConnectionsPerHost, int maxTotalConnections,
			int conTimeOutMs, int soTimeOutMs, List<RouteCfg> routeCfgList,
			HttpHost proxy) {
		// 使用默认的 socket factories 注册 "http" & "https" protocol scheme
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		supportedSchemes.register(new Scheme("https", 443, SSLSocketFactory
				.getSocketFactory()));
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(
				supportedSchemes);
		// 参数设置
		HttpParams httpParams = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,conTimeOutMs);
		httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOutMs);
		httpParams.setParameter("Accept-Encoding", "gzip,deflate,sdch");
		httpParams.setParameter("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");

		HttpProtocolParams.setUseExpectContinue(httpParams, false);
		connectionManager.setDefaultMaxPerRoute(maxConnectionsPerHost);
		connectionManager.setMaxTotal(maxTotalConnections);

		HttpClientParams.setCookiePolicy(httpParams,
				CookiePolicy.BROWSER_COMPATIBILITY);

		// 对特定路由修改最大连接数
		if (null != routeCfgList) {
			for (RouteCfg routeCfg : routeCfgList) {
				HttpHost localhost = new HttpHost(routeCfg.getHost(),
						routeCfg.getPort());
				connectionManager.setMaxForRoute(new HttpRoute(localhost),
						routeCfg.getMaxConnetions());
			}
		}

		httpClient = new DefaultHttpClient(connectionManager, httpParams);
		httpClient.setHttpRequestRetryHandler(httpRequestRetryHandler);
		httpContext = new BasicHttpContext();
		/*
		 * //设置代理 if(null!=proxy){
		 * httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		 * proxy); }
		 */
	}

	/**
	 * 重试策略
	 */
	protected static HttpRequestRetryHandler httpRequestRetryHandler;
	static {
		httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= StaticParameters.fetchRetryCount) {
					// 如果超过最大重试次数，那么就不要继续了
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					// 如果服务器丢掉了连接，那么就重试
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// 不要重试SSL握手异常
					return false;
				}
				HttpRequest request = (HttpRequest) context
						.getAttribute(ExecutionContext.HTTP_REQUEST);
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// 如果请求被认为是幂等的，那么就重试
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * Get方法传送消息（无压缩）
	 * 
	 * @param url
	 *            连接的URL
	 * @param queryString
	 *            请求参数串
	 * @return 服务器返回的信息
	 * @throws Exception
	 */
	public String simpleHttpGet(String url, String queryString,
			CookieStore cookieStore, HttpHost proxyHost) throws Exception {
		url=QHttpClient.formatURI(url);
		String responseData = null;
		if (queryString != null && !queryString.equals("")) {
			url += "?" + queryString;
		}
		log.info("QHttpClient simpleHttpGet [1] url = " + url);

		HttpGet httpGet = new HttpGet(url);
		if (cookieStore != null) {
			httpClient.setCookieStore(cookieStore);
		}
		if (proxyHost != null) {
			httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxyHost);
		}
		HttpResponse response;
		httpResponseCode = 0;
		response = httpClient.execute(httpGet, httpContext);
		httpResponseCode = response.getStatusLine().getStatusCode();
		log.info("QHttpClient simpleHttpGet [2] StatusLine : "
				+ response.getStatusLine());
		responseData = EntityUtils.toString(response.getEntity());
		httpGet.abort();
		log.info("QHttpClient simpleHttpGet [3] Response = "
				+ responseData.toString());

		return responseData.toString();
	}

	public String httpGet(String url) throws Exception {
		return this.httpGet(url, null, null, null);
	}

	public String httpGet(String url, CookieStore cookieStore) throws Exception {
		return this.httpGet(url, null, cookieStore, null);
	}

	public String httpGet(String url, CookieStore cookieStore,
			HttpHost proxyHost) throws Exception {
		return this.httpGet(url, null, cookieStore, proxyHost);
	}

	public static void main(String[] args) {
		QHttpClient test = new QHttpClient();
		try {
			test.httpGet("http://www.dianping.com/search/category/8/10/g117q666#breadCrumb");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Get方法传送消息
	 * 
	 * @param url
	 *            连接的URL
	 * @param queryString
	 *            请求参数串
	 * @param cookieStore
	 *            请求需要的cookie
	 * @param proxyHost
	 *            请求用到的代理服务器
	 * @return 服务器返回的信息
	 * @throws Exception
	 */
	public String httpGet(String url, String queryString,
			CookieStore cookieStore, HttpHost proxyHost) throws Exception {
		String responseData = null;
		url=QHttpClient.formatURI(url);
		if (StringUtils.hasText(queryString)) {
			url += "?" + queryString;
		}
		log.info("QHttpClient httpGet [1] url = " + url);

		HttpGet httpGet = new HttpGet(url);
		if (cookieStore != null) {
			httpClient.setCookieStore(cookieStore);
			// httpGet.addHeader("Cookie", COOKIE);
		}
		if (proxyHost != null) {
			httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxyHost);
		}
		httpGet.getParams().setParameter("Referer", url);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
		HttpResponse response;
		httpResponseCode = 0;
		response = httpClient.execute(httpGet, httpContext);
		httpResponseCode = response.getStatusLine().getStatusCode();
		log.info("QHttpClient httpGet [2] StatusLine : "
				+ response.getStatusLine());

		try {
			/*
			 * byte[] b=new byte[2048]; GZIPInputStream gzin = new
			 * GZIPInputStream(response.getEntity().getContent()); int length=0;
			 * while((length=gzin.read(b))!=-1){ responseData.append(new
			 * String(b,0,length)); } gzin.close();
			 */
			responseData = EntityUtils.toString(response.getEntity());
			httpGet.abort();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.abort();
		}
		log.info("QHttpClient httpGet [3] Response = ");

		return responseData;
	}

	/**
	 * Post方法传送消息
	 * 
	 * @param url
	 *            连接的URL
	 * @param queryString
	 *            请求参数串
	 * @return 服务器返回的信息
	 * @throws Exception
	 */
	public String httpPost(String url, String queryString) throws Exception {
		StringBuilder responseData = new StringBuilder();
		url=QHttpClient.formatURI(url);
		URI tmpUri = new URI(url);
		URI uri = URIUtils.createURI(tmpUri.getScheme(), tmpUri.getHost(),
				tmpUri.getPort(), tmpUri.getPath(), queryString, null);
		log.info("QHttpClient httpPost [1] url = " + uri.toURL());

		HttpPost httpPost = new HttpPost(uri);
		if (queryString != null && !queryString.equals("")) {
			StringEntity reqEntity = new StringEntity(queryString);
			// 设置类型
			reqEntity.setContentType("application/x-www-form-urlencoded");
			// 设置请求的数据
			httpPost.setEntity(reqEntity);
		}
		httpResponseCode = 0;
		HttpResponse response = httpClient.execute(httpPost, httpContext);
		log.info("QHttpClient httpPost [2] StatusLine = "
				+ response.getStatusLine());
		httpResponseCode = response.getStatusLine().getStatusCode();
		try {
			byte[] b = new byte[2048];
			GZIPInputStream gzin = new GZIPInputStream(response.getEntity()
					.getContent());
			int length = 0;
			while ((length = gzin.read(b)) != -1) {
				responseData.append(new String(b, 0, length));
			}
			gzin.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.abort();
		}
		log.info("QHttpClient httpPost [3] Response = "
				+ responseData.toString());
		return responseData.toString();
	}

	/**
	 * Post方法传送消息
	 * 
	 * @param url
	 *            连接的URL
	 * @param queryString
	 *            请求参数串
	 * @return 服务器返回的信息
	 * @throws Exception
	 */
	public String httpPostWithFile(String url, String queryString,
			List<NameValuePair> files) throws Exception {
		url=QHttpClient.formatURI(url);
		StringBuilder responseData = new StringBuilder();

		URI tmpUri = new URI(url);
		URI uri = URIUtils.createURI(tmpUri.getScheme(), tmpUri.getHost(),
				tmpUri.getPort(), tmpUri.getPath(), queryString, null);
		log.info("QHttpClient httpPostWithFile [1]  uri = " + uri.toURL());
		MultipartEntity mpEntity = new MultipartEntity();
		HttpPost httpPost = new HttpPost(uri);
		httpPost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		StringBody stringBody;
		FileBody fileBody;
		File targetFile;
		String filePath;
		FormBodyPart fbp;

		List<NameValuePair> queryParamList = QStrOperate
				.getQueryParamsList(queryString);
		for (NameValuePair queryParam : queryParamList) {
			stringBody = new StringBody(queryParam.getValue(),
					Charset.forName("UTF-8"));
			fbp = new FormBodyPart(queryParam.getName(), stringBody);
			mpEntity.addPart(fbp);
			// log.info("------- "+queryParam.getName()+" = "+queryParam.getValue());
		}

		for (NameValuePair param : files) {
			filePath = param.getValue();
			targetFile = new File(filePath);
			log.info("---------- File Path = " + filePath
					+ "\n---------------- MIME Types = "
					+ QHttpUtil.getContentType(targetFile));
			fileBody = new FileBody(targetFile,
					QHttpUtil.getContentType(targetFile), "UTF-8");
			fbp = new FormBodyPart(param.getName(), fileBody);
			mpEntity.addPart(fbp);

		}
		
		// log.info("---------- Entity Content Type = "+mpEntity.getContentType());
		httpPost.setEntity(mpEntity);
		httpResponseCode = 0;
		HttpResponse response = httpClient.execute(httpPost, httpContext);
		httpResponseCode = response.getStatusLine().getStatusCode();
		log.info("QHttpClient httpPostWithFile [2] StatusLine = "
				+ response.getStatusLine());

		try {
			byte[] b = new byte[2048];
			GZIPInputStream gzin = new GZIPInputStream(response.getEntity()
					.getContent());
			int length = 0;
			while ((length = gzin.read(b)) != -1) {
				responseData.append(new String(b, 0, length));
			}
			gzin.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.abort();
		}

		log.info("QHttpClient httpPostWithFile [3] Response = "
				+ responseData.toString());
		return responseData.toString();
	}

	/**
	 * 断开QHttpClient的连接
	 */
	public void shutdownConnection() {
		try {
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HttpContext getHttpContext() {
		return httpContext;
	}

	public void setHttpContext(HttpContext httpContext) {
		this.httpContext = httpContext;
	}

	public int getHttpResponseCode() {
		return httpResponseCode;
	}

	public void setHttpResponseCode(int httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

}
