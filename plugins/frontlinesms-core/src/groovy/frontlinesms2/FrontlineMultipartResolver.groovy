package frontlinesms2
import javax.servlet.http.HttpServletRequest
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.util.LinkedMultiValueMap

class FrontlineMultipartResolver extends CommonsMultipartResolver {
	@Override
	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) {
		def response
		try {
			def superResponse = super.resolveMultipart(request)
			response = superResponse
		}
		catch (MaxUploadSizeExceededException maxUploadSizeExceededException) {
			request.exception = maxUploadSizeExceededException
			response = new DefaultMultipartHttpServletRequest(request, new LinkedMultiValueMap(), [:], [:])
		}
		return response
	}
}
