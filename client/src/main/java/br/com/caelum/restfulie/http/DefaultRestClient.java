/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
 * All rights reserved.
 *
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
 */

package br.com.caelum.restfulie.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jvnet.inflector.Pluralizer;
import org.jvnet.inflector.lang.en.NounPluralizer;

import br.com.caelum.restfulie.RestClient;
import br.com.caelum.restfulie.RestfulieException;
import br.com.caelum.restfulie.http.apache.ApacheDispatcher;
import br.com.caelum.restfulie.mediatype.FormEncoded;
import br.com.caelum.restfulie.mediatype.JsonMediaType;
import br.com.caelum.restfulie.mediatype.MediaType;
import br.com.caelum.restfulie.mediatype.MediaTypes;
import br.com.caelum.restfulie.mediatype.XmlMediaType;
import br.com.caelum.restfulie.request.RequestDispatcher;

/**
 * Configured service entry point.
 *
 * @author guilherme silveira
 */
public class DefaultRestClient implements RestClient {

	private final MediaTypes types = new MediaTypes();

	private RequestDispatcher dispatcher;

	private Pluralizer inflector;

    private URI lastURI = null;
	
	private final ExecutorService threads;

	public DefaultRestClient() 
	{
		this.dispatcher = new ApacheDispatcher(this);
		this.inflector = new NounPluralizer();
		types.register(new XmlMediaType());
		types.register(new JsonMediaType());
		types.register(new FormEncoded());
		this.threads = Executors.newCachedThreadPool();
	}
	
	/**
	 * Constructor which only will register the specified media types
	 * @param medias MediaTypes to be registered with this client
	 * @author Felipe Brandao
	 */
	public DefaultRestClient( MediaType...medias ) 
	{
		this.dispatcher = new ApacheDispatcher(this);
		this.inflector = new NounPluralizer();
		
		for( MediaType media : medias ) this.types.register( media );
		
		this.threads = Executors.newCachedThreadPool();
	}
	
	public DefaultRestClient use(RequestDispatcher executor) {
		this.dispatcher = executor;
		return this;
	}

	public RequestDispatcher getProvider() {
		return dispatcher;
	}

	public MediaTypes getMediaTypes() {
		return types;
	}

	/**
	 * Entry point to direct access an uri.
	 */
	public Request at(URI uri) {
		lastURI = uri;
		return createRequestFor(uri);
	}

	/**
	 * Override this method to use your own Request object
	 * 
	 * @param uri
	 * @return
	 */
	protected Request createRequestFor(URI uri) {
		return new DefaultHttpRequest(uri, this);
	}

	/**
	 * Entry point to direct access an uri.
	 * @throws URISyntaxException
	 */
	public Request at(String uri) {
		try {
			return at(new URI(uri));
		} catch (URISyntaxException e) {
			throw new RestfulieException("Unable to build an URI for this request.", e);
		}
	}

	public URI lastURI() {
		return lastURI;
	}
	
	public Pluralizer inflectionRules() {
		return inflector;
	}

	public RestClient withInflector(Pluralizer inflector) 
	{
		this.inflector = inflector;
		return this;
	}
    
    public ExecutorService getThreads() {
        return threads;
    }

}
