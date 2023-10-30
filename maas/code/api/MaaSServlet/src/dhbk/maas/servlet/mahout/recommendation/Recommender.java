/************************************************************************
 * Copyright 2014	Le Dai Cat, Tran Sy Dat
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

***************************************************************************/
package dhbk.maas.servlet.mahout.recommendation;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Recommender
 */
@WebServlet("/Recommender")
public class Recommender extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public final String cmd1 = "/opt/mahout/bin/mahout recommenditembased -s SIMILARITY_LOGLIKELIHOOD -i ";
	public final String cmd2 = " -o outputrecommender --numRecommendations 25";
    public final String cmdget1 = "hadoop fs -getmerge outputrecommender /home/hadoop/";
    public final String cmdget2 = "/outputrecommender.txt";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Recommender() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter() ;
		
		String pathInput = request.getParameter("pathInput");
		String pathOutput = request.getParameter("pathOutput") ;
		
		if(pathInput != null && pathOutput != null) {
			deleteFileIfNeed(pathOutput);
			
			exc (this.cmd1 + pathInput + this.cmd2)	;
			exc (this.cmdget1 + pathOutput + this.cmdget2) ;
			
			out.print("SUCCESS");
		} else {
			out.print("FAILURE");
		}
	}
	
	private void deleteFileIfNeed (String pathOutput) {
		try {
			String cmd1 = "hadoop fs -rmr outputrecommender" ;
			String cmd2 = "hadoop fs -rmr temp" ;
			String cmd3 = "rm /home/hadoop/" + pathOutput + "/outputrecommender.txt" ;
			
			exc (cmd1) ;
			exc (cmd2) ;
			exc (cmd3) ;
		
		} catch (Exception e) {}
	}
	
	private Process exc (String cmd) throws IOException{
		Process p = null;
		p = Runtime.getRuntime().exec(cmd) ;
		p.getErrorStream() ;
		try {
			p.waitFor() ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			p.destroy(); 
		}
		return p ;
	}

}
