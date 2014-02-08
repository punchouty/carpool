		<%@ page import="grails.plugin.nimble.core.AdminsService"%>
		<n:isNotLoggedIn>
		<div class="nav-collapse collapse">
			<g:form controller="auth" action="signin" name="login-form" method="post"
				class="navbar-form form-inline pull-right">
				<input type="text" name="username" id="username" class="input-small"
					placeholder="User Id">
				<input type="password" name="password" id="password"
					class="input-small" placeholder="Password">
				<label class="checkbox"> 
					<g:checkBox name="rememberme" value="${rememberme}" /> Remember Me
				</label>
				<button type="submit" class="btn">Sign in</button>
				<div class="btn-group">
					<a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#">
						Help <span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<li>
							<a href="${request.contextPath}/faq">FAQ</a>
						</li>
						<li	<g:if test="${isSafety == 'true'}">class="active"</g:if>>
							<a href="${request.contextPath}/safety">Safety</a>
						</li>
						<li <g:if test="${isEtiquettes == 'true'}">class="active"</g:if>>
							<a href="${request.contextPath}/etiquettes">Etiquettes</a>
						</li>
						<li>
							<a href="${request.contextPath}/password/forgot">Forgot Password</a>
						</li>
						<li <g:if test="${isTerms == 'true'}">class="active"</g:if>><g:link
								controller="staticPage" action="terms">Terms</g:link>
						</li>
					</ul>
				</div>
				<g:if test="${isSignup == null || isSignup == false}">					
					<a class="btn btn-primary" href="${request.contextPath}/signup">Sign Up</a>
				</g:if>
				<input type="hidden" name="targetUri" value="${targetUri}" />
			</g:form>
			<h3 class="muted">raC looP</h3>
		</div>
		</n:isNotLoggedIn>
		<n:isLoggedIn>
		<div class="masthead">
			<ul class="nav nav-pills pull-right">
				<li	<g:if test="${isHome == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}">Home</a>
				</li>
				<li	<g:if test="${isSafety == 'true'}">class="active"</g:if>>
					<a href="${request.contextPath}/safety">Safety</a>
				</li>
				<li>
					<a href="${request.contextPath}/requests">Active Requests</a>
				</li>
				<li>
					<a href="${request.contextPath}/history">History</a>
				</li>
				<li>
					<a href="${request.contextPath}/notifications">Notifications <span class="badge badge-info">4</span></a>
				</li>
				<li class="dropdown">
					<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"> 
						${currentUser.profile.fullName}&nbsp;<i	class="icon-user"></i> <b class="caret"></b>
					</a>
					<ul class="dropdown-menu">
						<n:hasRole name="${AdminsService.ADMIN_ROLE}">
						<li>
							<g:link controller="admins" action="index">Admin Functions</g:link>
						</li>
						</n:hasRole>
						<li>
							<a href="${request.contextPath}/profile">Profile</a>
						</li>
						<li>
							<a href="${request.contextPath}/password/change">Change Password</a>
						</li>
						<li>
							<a href="${request.contextPath}/signout">Sign Out</a>
						</li>
					</ul>
				</li>		
			</ul>
			<g:link controller="staticPage" action="home"><h3 class="muted">raC looP</h3></g:link>
		</div>
		</n:isLoggedIn>