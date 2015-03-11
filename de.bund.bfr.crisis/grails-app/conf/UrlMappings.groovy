import grails.util.Environment


class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
		if (Environment.current == Environment.PRODUCTION) {
			"/"(controller: 'map', action: '/index')
		}
		else {
			"/"(view:"/index")
		}

        "500"(view:'/error')
	}
}
