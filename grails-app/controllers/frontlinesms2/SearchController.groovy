package frontlinesms2

class SearchController {
	def index = {
		redirect(action: "show", params: params)
	}
	
	def show = {
		[groupInstanceList : Group.findAll(),
			pollInstanceList: Poll.findAll()]
	}
	
	def search = {
		render(view:'show', model:show()<<[keywords: params.keywords])
	}
}

