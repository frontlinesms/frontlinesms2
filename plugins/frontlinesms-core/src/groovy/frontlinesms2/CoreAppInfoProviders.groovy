package frontlinesms2

class CoreAppInfoProviders {
	static void registerAll(AppInfoService s) {
		s.registerProvider('device_detection') { app, controller, data ->
			app.mainContext.deviceDetectionService.detected
		}

		s.registerProvider('connection_show') { app, controller, data ->
			def c = Fconnection.get(data.id)
			if(c) [id:c.id , status:c.status.toString()]
		}
	}
}

