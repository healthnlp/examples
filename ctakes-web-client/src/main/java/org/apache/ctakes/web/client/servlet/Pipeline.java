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

import java.io.FileNotFoundException;

import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.resource.FileResourceImpl;
import org.apache.ctakes.dictionary.lookup2.ae.AbstractJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.DefaultJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.JCasTermAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ResourceInitializationException;


public class Pipeline {	

	public static AggregateBuilder getAggregateBuilder() throws Exception {
		AggregateBuilder builder = new AggregateBuilder();
		//builder.add(ClinicalPipelineFactory.getFastPipeline());
	      builder.add( ClinicalPipelineFactory.getTokenProcessingPipeline() );
	      builder.add( AnalysisEngineFactory.createEngineDescription( DefaultJCasTermAnnotator.class,
	               AbstractJCasTermAnnotator.PARAM_WINDOW_ANNOT_KEY,
	               "org.apache.ctakes.typesystem.type.textspan.Sentence",
	               JCasTermAnnotator.DICTIONARY_DESCRIPTOR_KEY,
	               "org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab.xml" )
	         );
//	      builder.add( PolarityCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( UncertaintyCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( HistoryCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( ConditionalCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( GenericCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( SubjectCleartkAnalysisEngine.createAnnotatorDescription() );		
		return builder;
	}

}
