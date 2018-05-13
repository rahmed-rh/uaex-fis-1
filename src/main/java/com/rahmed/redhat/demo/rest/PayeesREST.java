package com.rahmed.redhat.demo.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayeesREST extends RouteBuilder{
	
	
	private InfinispanIdempotentRepository infinispanRepository;
	
	
	@Autowired
	public void setInfinispanRepository(InfinispanIdempotentRepository infinispanRepository) {
		this.infinispanRepository = infinispanRepository;
	}
	
    @Override
    public void configure() {       
    	
    	
    	rest("/payees").description("Payees service")

	    	.get("/").description("List Payees")
				.route().routeId("payees-list")
				.bean(PayeesService.class,"listPayees")
				.endRest()

    		.post("/").type(Payee.class).description("Create a new Payee")
    			.route().routeId("insert-payee").tracing()
    			.log("Order Id is ${body.id}")
    			.setHeader("id",simple("${body.id}"))
    			.log("Order Id is ${header.id} whole body is ${body}")
    			.idempotentConsumer(header("id"),
    					infinispanRepository).skipDuplicate(true)
    			.log("inserting new order ${body}")
    			.setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.PUT_IF_ABSENT))
    	        .setHeader(InfinispanConstants.KEY, simple("${body.id}"))
    			//.bean(PayeesService.class,"createPayee")
    	        .to("infinispan:remote?cacheContainer=#remoteCacheContainer")
    			.transform(constant("OK"))
    			.endRest();
    	
    	
    	
      
    			
	}
}
