import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
//import java.security.KeyStore.TrustedCertificateEntry;
import java.sql.*;

import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class crawling_mysql_connection {

	static Document doc;
	static Document url;
	//	static int contentindex;
	//	static JSONArray contentarray = new JSONArray();  //내용url을 담는 제이슨 배열 

	static String[] urllist = new String[100];       //url리스트 배열

	static String[] numberlist = new String[100];   //번호 배열
	static String[] titlelist = new String[100];    //제목 배열
	static String[] writerlist = new String[100];   //작성자 배열
	static String[] timelist = new String[100];     //작성날짜 배열

	static int numberindex;

	static String pretime;
	static String posttime;

	public static void main(String[] args) throws Exception{

		TrustManager[] trustAllCerts = new TrustManager[]{    //로그인 뚫기
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Now you can access an https URL without having the certificate in the truststore
		try{
			URL url = new URL("http://computer.cnu.ac.kr/");
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Crawling();  //웹크롤링 실행
		MysqlConnection();  //mysql에 데이터 입력
		RequestToFirebase();
	}

	public static void MysqlConnection() throws Exception {

		String host = "localhost:3306";
		String database = "CSE";
		String user = "phpmyadmin";
		String password = "kwonmysql";

		// check that the driver is installed
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("드라이버 연결 성공");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("드라이버 연결 실패");
			throw new ClassNotFoundException("MySQL JDBC driver NOT detected in library path.", e);
		}

		System.out.println("MySQL JDBC driver detected in library path.");

		Connection connection = null;

		// Initialize connection object
		try
		{
			String url = String.format("jdbc:mysql://%s/%s", host, database);

			// Set connection properties.
			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("useSSL", "true");
			properties.setProperty("verifyServerCertificate", "true");
			properties.setProperty("requireSSL", "false");

			// get connection
			connection = DriverManager.getConnection(url, properties);
			System.out.println("디비 연결 성공");
		}
		catch (SQLException e)
		{
			System.out.println("디비 연결 실패");
			throw new SQLException("Failed to create connection to database.", e);
		}

		if (connection != null)   //데이터 베이스 업데이트 부분
		{ 
			System.out.println("Successfully created connection to database.");

			// Perform some SQL queries over the connection.
			try{
				// Modify some data in table.
				Statement statement = connection.createStatement();  //이미 저장되어 있는 날짜를 불러오는 sql문
				ResultSet results = statement.executeQuery("SELECT * from `Information1`;");
				results.next();  //results문의 첫줄로 이동
				pretime = results.getString("time");  //이전 날짜

				int nRowsUpdated = 0;  //추가된 행의 개수
				PreparedStatement Deletesql = connection.prepareStatement("TRUNCATE `Information1`;");   //데이터를 테이블에 업데이트 하기 전에 테이블을 초기화
				Deletesql.executeUpdate();
				for(int i=0; i<numberindex; i++) {  //mysql에 크롤링한 데이터 입력
					PreparedStatement Insertsql = connection.prepareStatement("INSERT INTO `Information1` VALUES (?,?,?,?,?,?);"); //sql문 작성 number, title, writer, time, no  
					Insertsql.setString(1, numberlist[i]);  //number 추가
					Insertsql.setString(2, titlelist[i]);  //title 추가
					Insertsql.setString(3, writerlist[i]);  //wrtier 추가
					Insertsql.setString(4, timelist[i]);  //time 추가
					Insertsql.setString(5, urllist[i]);   //url 추가
					Insertsql.setString(6, String.valueOf(nRowsUpdated));  //no 추가
					nRowsUpdated += Insertsql.executeUpdate();  //추가된 행의 개수가 executeUpdate()메소드의 리턴 값
				} 
				posttime = timelist[0];
				System.out.println(pretime);
				System.out.println(posttime);

				if(!pretime.equals(posttime)) {  //내용이 업데이트 될 경우 파이어베이스에 푸시 요청
					System.out.println("Information Update");
					RequestToFirebase();
				}
				// NOTE No need to commit all changes to database, as auto-commit is enabled by default.
			}
			catch (SQLException e)
			{
				throw new SQLException("Encountered an error when executing given sql statement.", e);
			}       
		}
		else {
			System.out.println("Failed to create connection to database.");
		}
	}
	public static void Crawling(){    //크롤링을 실행하는 클래스

		try{
			//			FileWriter fw = new FileWriter("output1.txt");
			doc = Jsoup.connect("http://computer.cnu.ac.kr/index.php?mid=notice").get();  //Jsoup라이브러리의 connect함수를 이용해 크롤링한 내용을 doc에 저장
			url = Jsoup.connect("http://computer.cnu.ac.kr/index.php?mid=notice").get();

			Elements number = doc.select("tbody>tr>.no");   //공지사항의 번호 가져옴
			Elements title = doc.select("tbody>tr>.title");  //공지사항의 제목 가져옴
			Elements writer = doc.select("tbody>tr>.author");  //공지사항의 작성자 가져옴
			Elements time = doc.select("tbody>tr>.time");   //공지사항의 작성날짜 가져옴

			Elements urladdress = url.select(".title>a");  //항목의 url

			int titleindex =0; int writerindex =0; int timeindex =0;    //각 항목의 데이터들을 배열에 저장
			for(Element numbers : number){   
				numberlist[numberindex] = numbers.text();
				numberindex++;
			}
			System.out.println();
			for(Element titles : title) {
				titlelist[titleindex++] = titles.text();
			}
			System.out.println();
			for(Element writers : writer) {
				writerlist[writerindex++] = writers.text();
			}
			System.out.println();
			for(Element times : time) {
				timelist[timeindex++] = times.text();
			}
			int i=0;
			for (Element urladdresses : urladdress) {
				urllist[i] = urladdresses.attr("href");   //url을 저장하는 배열 생성
				i++;
			}
			//			int j =0;
			//			while(urllist[j] !=null) {   //항목들의 주소값들을 배열에 입력
			//				logincrawling(urllist[j]);
			//				j++;
			//			}


			//			for(int a =0; a<numberindex; a++) {
			//				System.out.println(a+" "+numberlist[a]+" "+titlelist[a]+" "+writerlist[a]+" "+timelist[a]);
			//				fw.write("\""+numberlist[a]+"\",\""+titlelist[a]+"\",\""+writerlist[a]+"\",\""+timelist[a]+"\",\""+a+"\"\r\n");
			//			}
			//			fw.flush();
			//			fw.close();
			//			String contentjsoninfo = contentarray.toJSONString();  //항목 내용에 관한 제이슨파일 내용을 contentjsoninfo변수에 저장
			//			FileWriter file1 = new FileWriter("information1content.json");
			//			file1.write(contentjsoninfo.toString());
			//			file1.flush();
			//			file1.close();
		}
		catch(IOException e){  //예외 처리
			e.printStackTrace();
		}
	}

	public static void RequestToFirebase() throws IOException, NoSuchAlgorithmException, KeyManagementException {  //Firebase에 푸시 메세지를 요청하는 메소드

		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
			public X509Certificate[] getAcceptedIssuers(){return new X509Certificate[0];}
			public void checkClientTrusted(X509Certificate[] certs, String authType){}
			public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		URL obj = new URL("https://fcm.googleapis.com/fcm/send");
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "key=AAAAPsM4yVA:APA91bF8BfRa6fUl8YQDMp969-Pwi2ZJdo_1RmsZ9bHLiYbhAfVrPG9qO9GillOuhVf7l-BgU62Dxkf_NYqtuWqfuDXe2RWFbPhK1ADxGv60wIGZiBcSBSuz-ZV1KsPrcPJRD-nl1tNa");
		String urlParameters = "{"+"\"data\": {"+"\"message\": \"CSE NEWS가 업데이트 되었습니다!!!\""+"},"+"\"to\": \"/topics/noticeMsg\""+"}";

             //post request
             con.setDoOutput(true);
             DataOutputStream wr = new DataOutputStream(con.getOutputStream());
             wr.write(urlParameters.getBytes("UTF-8"));
             wr.flush();
             wr.close();

             int responseCode = con.getResponseCode();     
             System.out.println("Post parameters : " + urlParameters);
             System.out.println("Response Code : " + responseCode);

             StringBuffer response = new StringBuffer();

             if(responseCode == 200){   
            	 BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            	 String inputLine;
            	 while ((inputLine = in.readLine()) != null) {
            		 response.append(inputLine);
            	 }
            	 in.close();   
             }
             //result
             System.out.println(response.toString());
	}


	//	public static void logincrawling(String crawlingurl) throws IOException {   //로그인 후의 공지사항 내용을 크롤링하는 함수
	//		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
	//
	//		Connection.Response loginPageResponse = Jsoup.connect("https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
	//				.timeout(3000)
	//				.header("Origin", "http://computer.cnu.ac.kr/")
	//				.header("Referer", "https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
	//				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
	//				.header("Content-Type", "application/x-www-form-urlencoded")
	//				.header("Accept-Encoding", "gzip, deflate, br")
	//				.header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
	//				.method(Connection.Method.GET)
	//				.execute();
	//
	//		//로그인 페이지에서 얻은 쿠키
	//		Map<String, String> loginTryCookie = loginPageResponse.cookies();
	//
	//		//로그인 페이지에서 로그인에 함께 전송하는 토큰 얻어내기
	//		Document loginPageDocument = loginPageResponse.parse();
	//
	//		Map<String,String> log = new HashMap<>();
	//		log.put("user_id","u201302362");
	//		log.put("password","dhtns3709");
	//		log.put("ruleset","@login");
	//		log.put("act","procMemberLogin");
	//		log.put("error_return_url", "/index.php?act=dispMemberLoginForm");
	//		log.put("mid", "smain");
	//		log.put("vid", "");
	//		log.put("success_return_url", "https://computer.cnu.ac.kr/index.php?act=procMemberLogin");
	//		log.put("xe_validator_id", "modules/member/skins");
	//		log.put("keep_signed", "Y");
	//
	//		Connection.Response response = Jsoup.connect("https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
	//				.userAgent(userAgent)
	//				.timeout(3000)
	//				.header("Origin", "http://computer.cnu.ac.kr/")
	//				.header("Referer", "https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
	//				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
	//				.header("Content-Type", "application/x-www-form-urlencoded")
	//				.header("Accept-Encoding", "gzip, deflate, br")
	//				.header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
	//				.cookies(loginTryCookie)
	//				.data(log)
	//				.method(Connection.Method.POST)
	//				.execute();
	//		Map<String, String> loginCookie = response.cookies();
	//
	//		Document adminPageDocument = Jsoup.connect(crawlingurl)   //해당 항목의 내용을 크롤링
	//				.userAgent(userAgent)
	//				.header("Referer", "http://computer.cnu.ac.kr")
	//				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
	//				.header("Content-Type", "application/x-www-form-urlencoded")
	//				.header("Accept-Encoding", "gzip, deflate, sdch")
	//				.header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
	//				.cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
	//				.get();
	//
	//		//select 내의 option 태그 요소들
	//		Elements str = adminPageDocument.select("div.rd_hd.clear");
	//		String contenturl = str.html();  //항목 내용을 나타내는 html
	//
	//	
	//		JSONObject contentjsonObject = new JSONObject();  //제이슨 객체 생성
	//		contentjsonObject.put("number", contentindex);
	//		contentjsonObject.put("text", contenturl);
	//		contentarray.add(contentjsonObject);
	//		contentindex++;
	//	}


	//    public String index(Model model, HttpServletRequest request, HttpSession session, MobileTokenVO vo)throws Exception{
	//            
	//            List<MobileTokenVO> tokenList = fcmService.loadFCMInfoList(vo); 
	//            
	//                String token = tokenList.get(count).getDEVICE_ID();
	//                
	//                final String apiKey = "파이어 베이스의 서버 API키를 여기에 넣는다";
	//                URL url = new URL("https://fcm.googleapis.com/fcm/send");
	//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	//                conn.setDoOutput(true);
	//                conn.setRequestMethod("POST");
	//                conn.setRequestProperty("Content-Type", "application/json");
	//                conn.setRequestProperty("Authorization", "key=" + apiKey);
	// 
	//                conn.setDoOutput(true);
	//                
	//                String userId =(String) request.getSession().getAttribute("ssUserId");
	// 
	//                // 이렇게 보내면 주제를 ALL로 지정해놓은 모든 사람들한테 알림을 날려준다.
	//                String input = "{\"notification\" : {\"title\" : \"여기다 제목 넣기 \", \"body\" : \"여기다 내용 넣기\"}, \"to\":\"/topics/ALL\"}";
	//                
	//                // 이걸로 보내면 특정 토큰을 가지고있는 어플에만 알림을 날려준다  위에 둘중에 한개 골라서 날려주자
	//                String input = "{\"notification\" : {\"title\" : \" 여기다 제목넣기 \", \"body\" : \"여기다 내용 넣기\"}, \"to\":\" 여기가 받을 사람 토큰  \"}";
	// 
	//                OutputStream os = conn.getOutputStream();
	//                
	//                // 서버에서 날려서 한글 깨지는 사람은 아래처럼  UTF-8로 인코딩해서 날려주자
	//                os.write(input.getBytes("UTF-8"));
	//                os.flush();
	//                os.close();
	// 
	//                int responseCode = conn.getResponseCode();
	//                System.out.println("\nSending 'POST' request to URL : " + url);
	//                System.out.println("Post parameters : " + input);
	//                System.out.println("Response Code : " + responseCode);
	//                
	//                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	//                String inputLine;
	//                StringBuffer response = new StringBuffer();
	// 
	//                while ((inputLine = in.readLine()) != null) {
	//                    response.append(inputLine);
	//                }
	//                in.close();
	//                // print result
	//                System.out.println(response.toString());
	//                
	// 
	//        return "jsonView";
	//    }

}
