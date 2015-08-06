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

import org.apache.ctakes.assertion.medfacts.cleartk.PolarityCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.UncertaintyCleartkAnalysisEngine;
import org.apache.ctakes.chunker.ae.Chunker;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.constituency.parser.ae.ConstituencyParser;
import org.apache.ctakes.contexttokenizer.ae.ContextDependentTokenizerAnnotator;
import org.apache.ctakes.core.ae.SentenceDetector;
import org.apache.ctakes.core.ae.SimpleSegmentAnnotator;
import org.apache.ctakes.core.ae.TokenizerAnnotatorPTB;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.resource.FileResourceImpl;
import org.apache.ctakes.dependency.parser.ae.ClearNLPDependencyParserAE;
import org.apache.ctakes.dependency.parser.ae.ClearNLPSemanticRoleLabelerAE;
import org.apache.ctakes.dictionary.lookup2.ae.AbstractJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.DefaultJCasTermAnnotator;
import org.apache.ctakes.dictionary.lookup2.ae.JCasTermAnnotator;
import org.apache.ctakes.lvg.ae.LvgAnnotator;
import org.apache.ctakes.postagger.POSTagger;
import org.apache.ctakes.temporal.ae.BackwardsTimeAnnotator;
import org.apache.ctakes.temporal.ae.DocTimeRelAnnotator;
import org.apache.ctakes.temporal.ae.EventAnnotator;
import org.apache.ctakes.temporal.ae.EventEventRelationAnnotator;
import org.apache.ctakes.temporal.ae.EventTimeRelationAnnotator;
import org.apache.ctakes.temporal.ae.EventTimeSelfRelationAnnotator;
import org.apache.ctakes.temporal.eval.Evaluation_ImplBase.CopyNPChunksToLookupWindowAnnotations;
import org.apache.ctakes.temporal.eval.Evaluation_ImplBase.RemoveEnclosedLookupWindows;
import org.apache.ctakes.temporal.pipelines.FullTemporalExtractionPipeline.CopyPropertiesToTemporalEventAnnotator;
import org.apache.ctakes.typesystem.type.refsem.Event;
import org.apache.ctakes.typesystem.type.refsem.EventProperties;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.Lists;


public class Pipeline {	

	public static AggregateBuilder getAggregateBuilder() throws Exception {
		AggregateBuilder builder = new AggregateBuilder();
		//builder.add(ClinicalPipelineFactory.getFastPipeline());
//	      builder.add( ClinicalPipelineFactory.getTokenProcessingPipeline() );
		  builder.add(AnalysisEngineFactory.createEngineDescription(SimpleSegmentAnnotator.class));
		  builder.add( SentenceDetector.createAnnotatorDescription() );
	      builder.add( TokenizerAnnotatorPTB.createAnnotatorDescription() );
	      builder.add( LvgAnnotator.createAnnotatorDescription() );
	      builder.add( ContextDependentTokenizerAnnotator.createAnnotatorDescription() );
	      builder.add( POSTagger.createAnnotatorDescription() );
	      builder.add( Chunker.createAnnotatorDescription() );
	      builder.add( ClinicalPipelineFactory.getStandardChunkAdjusterAnnotator() );

	      builder.add( AnalysisEngineFactory.createEngineDescription( CopyNPChunksToLookupWindowAnnotations.class ) );
	      builder.add( AnalysisEngineFactory.createEngineDescription( RemoveEnclosedLookupWindows.class ) );
	      try {
	         builder.add( AnalysisEngineFactory.createEngineDescription( DefaultJCasTermAnnotator.class,
	               AbstractJCasTermAnnotator.PARAM_WINDOW_ANNOT_PRP,
	               "org.apache.ctakes.typesystem.type.textspan.Sentence",
	               JCasTermAnnotator.DICTIONARY_DESCRIPTOR_KEY,
	               ExternalResourceFactory.createExternalResourceDescription(
	                     FileResourceImpl.class,
	                     FileLocator.locateFile( "org/apache/ctakes/dictionary/lookup/fast/cTakesHsql.xml" ) )
	         ) );
	      } catch ( FileNotFoundException e ) {
	         e.printStackTrace();
	         throw new ResourceInitializationException( e );
	      }
	      
	      builder.add( ClearNLPDependencyParserAE.createAnnotatorDescription() );
	      builder.add( PolarityCleartkAnalysisEngine.createAnnotatorDescription() );
	      builder.add( UncertaintyCleartkAnalysisEngine.createAnnotatorDescription() );
	      builder.add( AnalysisEngineFactory.createEngineDescription( ClearNLPSemanticRoleLabelerAE.class ) );
	      builder.add( AnalysisEngineFactory.createEngineDescription( ConstituencyParser.class ) );
	      	
			// Add BackwardsTimeAnnotator
			builder.add(BackwardsTimeAnnotator
					.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/timeannotator/model.jar"));
			// Add EventAnnotator
			builder.add(EventAnnotator
					.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/eventannotator/model.jar"));
			builder.add( AnalysisEngineFactory.createEngineDescription( CopyPropertiesToTemporalEventAnnotator.class ) );
			// Add Document Time Relative Annotator
			//link event to eventMention
			builder.add(AnalysisEngineFactory.createEngineDescription(AddEvent.class));
			builder.add(DocTimeRelAnnotator
   					.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/doctimerel/model.jar"));
			// Add Event to Event Relation Annotator
			builder.add(EventTimeSelfRelationAnnotator
					.createEngineDescription("/org/apache/ctakes/temporal/ae/eventtime/20150629/model.jar"));
			// Add Event to Event Relation Annotator
			builder.add(EventEventRelationAnnotator
					.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/eventevent/20150630/model.jar"));
			
						
//	      builder.add( PolarityCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( UncertaintyCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( HistoryCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( ConditionalCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( GenericCleartkAnalysisEngine.createAnnotatorDescription() );
//	      builder.add( SubjectCleartkAnalysisEngine.createAnnotatorDescription() );		
		return builder;
	}

	public static class AddEvent extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
		@Override
		public void process(JCas jCas) throws AnalysisEngineProcessException {
			for (EventMention emention : Lists.newArrayList(JCasUtil.select(
					jCas,
					EventMention.class))) {
				EventProperties eventProperties = new org.apache.ctakes.typesystem.type.refsem.EventProperties(jCas);

				// create the event object
				Event event = new Event(jCas);

				// add the links between event, mention and properties
				event.setProperties(eventProperties);
				emention.setEvent(event);

				// add the annotations to the indexes
				eventProperties.addToIndexes();
				event.addToIndexes();
			}
		}
	}
}
