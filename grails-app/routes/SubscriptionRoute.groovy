

class SubscriptionRoute {
    def configure = {
		from("seda:input.queue").to("bean:subscriptionService?method=process")
    }
}
