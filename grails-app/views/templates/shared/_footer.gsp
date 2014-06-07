		<div class="navbar-inner">
            <ul class="container-fluid">
            	<li>
            		&copy; racloop 2014
            	</li>
				<li	<g:if test="${isSafety == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/safety">Safety</a>
				</li>
				<li <g:if test="${isEtiquettes == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/etiquettes">Etiquettes</a>
				</li>	
				<li	<g:if test="${isFaq == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/faq">FAQ</a>
				</li>
            </ul>
            <span class="mega-octicon octicon-mark-racloop" title="raC looP">Ez</span>
            <ul class="site-footer-links right"> 
				<li <g:if test="${isTerms == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/terms">Terms</a>
				</li>	
				<li <g:if test="${isPrivacy == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/privacy">Privacy</a>
				</li>  
				<li <g:if test="${isAbout == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/about">About</a>
				</li>	    
            </ul>
        </div>