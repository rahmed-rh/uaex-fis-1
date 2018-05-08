package com.rahmed.redhat.demo.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

@Component
public class PayeesREST extends RouteBuilder{
	
    @Override
    public void configure() {       
    	onException(java.sql.SQLIntegrityConstraintViolationException.class)
	    	.handled(true)
	    	.setBody(simple("Order id already registered"));
    	
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
                        MemoryIdempotentRepository.memoryIdempotentRepository(200)).skipDuplicate(true)
    			.log("inserting new order ${body}")			
    			.bean(PayeesService.class,"createPayee")
    			.transform(constant("OK"))
    			.endRest();
    			
	}
}
