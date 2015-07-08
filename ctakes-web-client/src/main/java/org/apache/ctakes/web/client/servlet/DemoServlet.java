/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ctakes.web.client.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.ctakes.core.cc.pretty.plaintext.PrettyTextWriter;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.log4j.Logger;

/*
 * Servlet that wires up a cTAKES pipeline
 * NOT Thread Safe. Pipeline is shared in JVM
 * For illustration purposes only.
 */
public class DemoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(DemoServlet.class);
	private static final NumberFormat formatter = new DecimalFormat("#0.00000");

	// Reuse the pipeline for demo purposes
	static AnalysisEngine pipeline;

	public void init() throws ServletException {
		LOGGER.info("Initilizing Pipeline...");
		AggregateBuilder aggregateBuilder;
		try {
			aggregateBuilder = Pipeline.getAggregateBuilder();
			pipeline = aggregateBuilder.createAggregate();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long start = System.currentTimeMillis();
		PrintWriter out = response.getWriter();
		String text = request.getParameter("q");
		String format = request.getParameter("format");
		LOGGER.info("###\n" + text + "###\n");
		if (text != null && text.trim().length() > 0) {
			try {
				/*
				 * Set the document text to process And run the cTAKES pipeline
				 */
				JCas jcas = pipeline.newJCas();
				jcas.setDocumentText(text);
				pipeline.process(jcas);
				String result = formatResults(jcas, format, response);
				jcas.reset();
				String elapsed = formatter
						.format((System.currentTimeMillis() - start) / 1000d);
				if ("html".equalsIgnoreCase(format)
						|| "pretty".equalsIgnoreCase(format)) {
					result += "<p/><i> Processed in " + elapsed + " secs</i>";
				}
				out.println(result);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public String formatResults(JCas jcas, String format,
			HttpServletResponse response) throws Exception {
		StringBuffer sb = new StringBuffer();
		/**
		 * Select the types/classes that you are interested We are selecting
		 * everything including TOP for demo purposes
		 */
		Collection<TOP> annotations = JCasUtil.selectAll(jcas);
		if ("html".equalsIgnoreCase(format)) {
			response.setContentType("text/html");

			sb.append("<html><head><title></title></head><body><table>");
			for (TOP a : annotations) {

				sb.append("<tr>");
				sb.append("<td>" + a.getType().getShortName() + "</td>");
				extractFeatures(sb, (FeatureStructure) a);
				sb.append("</tr>");
			}
			sb.append("</table></body></html>");
		} else if ("pretty".equalsIgnoreCase(format)) {
			StringWriter sw = new StringWriter();
			BufferedWriter writer = new BufferedWriter(sw);
			Collection<Sentence> sentences = JCasUtil.select(jcas,
					Sentence.class);
			for (Sentence sentence : sentences) {
				PrettyTextWriter.writeSentence(jcas, sentence, writer);
			}
			writer.close();
			sb.append("<html><head><title></title></head><body><table><pre>");
			sb.append(sw.toString());
			sb.append("</pre></table></body></html>");

		} else {
			response.setContentType("application/xml");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			XmiCasSerializer.serialize(jcas.getCas(), output);
			sb.append(output.toString());
			output.close();
		}
		return sb.toString();
	}

	public void extractFeatures(StringBuffer sb, FeatureStructure fs) {

		List<?> plist = fs.getType().getFeatures();
		for (Object obj : plist) {
			if (obj instanceof Feature) {
				Feature feature = (Feature) obj;
				String val = "";
				if (feature.getRange().isPrimitive()) {
					val = fs.getFeatureValueAsString(feature);
				} else if (feature.getRange().isArray()) {
					// Flatten the Arrays
					FeatureStructure featval = fs.getFeatureValue(feature);
					if (featval instanceof FSArray) {
						FSArray valarray = (FSArray) featval;
						for (int i = 0; i < valarray.size(); ++i) {
							FeatureStructure temp = valarray.get(i);
							extractFeatures(sb, temp);
						}
					}
				}
				if (feature.getName() != null
						&& val != null
						&& val.trim().length() > 0
						&& !"confidence".equalsIgnoreCase(feature
								.getShortName())) {
					sb.append("<td>" + feature.getShortName() + "</td>");
					sb.append("<td>" + val + "</td>");
				}
			}
		}

	}

}
