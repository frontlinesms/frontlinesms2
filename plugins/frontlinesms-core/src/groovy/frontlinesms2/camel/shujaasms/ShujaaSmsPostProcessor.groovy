package frontlinesms2.camel.shujaasms

import frontlinesms2.*
import frontlinesms2.camel.exception.*
import org.apache.camel.*

class ShujaaSmsPostProcessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		def log = { println "ShujaaSmsPostProcessor.process() : $it" }
		log 'ENTRY'
		log "in.body:" + exchange.in.body
		byte[] bytes = exchange.in.getBody(byte[].class);
		log "in.body as byte[]:" + bytes
		String text = new String(bytes, "UTF-8").trim();
		log "in.body as byte[] as String:" + text
		log "in.body got as a string" + exchange.in.getBody(String.class)
		if(text ==~ "ID:.*"){
			 log "message sent successfully"
		}else {
		if(text.endsWith(":0")){
			log "The message has been accepted by the gateway for delivery."
		}else if(text.endsWith(":1")){
			log "The delivery to the destination address is successful."
		}else if(text.endsWith(":2")){
			log "The delivery to the destination address has not been successful."
			throw new AuthenticationException("Shujaa SMS Gateway : The delivery to the destination address has not been successful.")
		}else if(text.endsWith(":4")){
			log "The message has been queued for delivery."
		}else if(text.endsWith(":8")){
			log "The message has been submitted to the operator SMS gateway."
		}else if(text.endsWith(":16")){
			log "The message has been rejected by the operator SMS gateway."
			throw new AuthenticationException("Shujaa SMS Gateway : The message has been rejected by the operator SMS gateway.")
		}else if(text.endsWith(":32")){
			log "Intermediate notifications by the operator SMS gateway."
		}else if(text.endsWith(":200")){
			log "The username or email used to authenticate is not registered with the gateway."
			throw new AuthenticationException("Shujaa SMS Gateway : The username or email used to authenticate is not registered with the gateway.")
		}else if(text.endsWith(":201")){
			log "The password supplied does not match the username."			
			throw new AuthenticationException("Shujaa SMS Gateway : The password supplied does not match the username.")
		}else if(text.endsWith(":202")){
			log "The account from which the send attempt has been made is not active, it may be in a suspended or deleted state."
			throw new AuthenticationException("Shujaa SMS Gateway : The account from which the send attempt has been made is not active, it may be in a suspended or deleted state.")
		}else if(text.endsWith(":203")){
			log "The destination address is not a valid MSISDN or is a destination that is supported by the gateway."
			throw new AuthenticationException("Shujaa SMS Gateway : The destination address is not a valid MSISDN or is a destination that is supported by the gateway.")
		}else if(text.endsWith(":204")){
			log "The source address is not allowed to be used by the particular account."
			throw new AuthenticationException("Shujaa SMS Gateway : The source address is not allowed to be used by the particular account.")
		}else if(text.endsWith(":205")){
			log "Not enough or invalid parameters have been provided when submitting a request to send a message."
			throw new AuthenticationException("Shujaa SMS Gateway : Not enough or invalid parameters have been provided when submitting a request to send a message.")
		}else if(text.endsWith(":206")){
			log "An internal problem with the gateway is preventing the delivery of the message."
			throw new AuthenticationException("Shujaa SMS Gateway : An internal problem with the gateway is preventing the delivery of the message.")
		}else if(text.endsWith(":207")){
			log "The user has exhausted the credits allowed for bulk SMS."
			throw new AuthenticationException("Shujaa SMS Gateway : The user has exhausted the credits allowed for bulk SMS.")
		}else if(text.endsWith(":208")){
			log "The message format is invalid, for example there is no content in the message."
			throw new AuthenticationException("Shujaa SMS Gateway : The message format is invalid, for example there is no content in the message.")
		}else if(text.endsWith(":209")){
			log "The phone number prefix provided is not in the range handled by this gateway."
			throw new AuthenticationException("Shujaa SMS Gateway : The phone number prefix provided is not in the range handled by this gateway.")
		}
		}
		log 'EXIT'
	}
}