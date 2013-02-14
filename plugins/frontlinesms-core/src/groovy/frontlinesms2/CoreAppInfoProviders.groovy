package frontlinesms2

class CoreAppInfoProviders {
	static void registerAll(AppInfoService s) {
		s.registerProvider('device_detection') { app, controller ->
			app.mainContext.deviceDetectionService.detected
		}
	}
}

