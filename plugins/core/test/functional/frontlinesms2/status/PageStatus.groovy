package frontlinesms2.status

import frontlinesms2.*

class PageStatus extends geb.Page {
	static getUrl() { 'status' }
	static at = {
		title.startsWith('Status')
	}
	static content = {
		statusButton { $('#update-chart') }
		detectModems { $('#device-detection a') }
		detectedDevicesSection { $('div#device-detection') }
		noDevicesDetectedNotification(required:false) { detectedDevicesSection.find('p') }
		detectedDevicesTable(required:false) { detectedDevicesSection.find('table') }
		trafficForm { $("#trafficForm")}
		typeFilters(required:false) { $("#type-filters")}
		activityFilter(required:false) { typeFilters.find("#activityId")}
	}
}
