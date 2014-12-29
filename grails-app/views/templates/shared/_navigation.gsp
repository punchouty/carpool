		<%@ page import="grails.plugin.nimble.core.AdminsService"%>	
		<%@page import="grails.plugin.nimble.core.UserBase"%>
		<%@ page import="org.apache.shiro.SecurityUtils"%>	
		<g:set var="currentUser" value="${UserBase.get(SecurityUtils.subject?.principal)}" />	
		<!-- STICKY NAVIGATION -->
            <div <g:if test="${isLandingPage}">class="navbar navbar-inverse bs-docs-nav navbar-fixed-top sticky-navigation appear-on-scroll"</g:if><g:else>class="navbar navbar-inverse bs-docs-nav navbar-fixed-top sticky-navigation"</g:else> role="navigation">
                <div class="container">
                    <div class="navbar-header">

                        <!-- LOGO ON STICKY NAV BAR -->
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#stamp-navigation">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-grid-2x2"></span>
                        </button>

                        <!-- LOGO -->
                        <a class="navbar-brand logo-racloop" href="${request.contextPath}/">
                            RACLOOP
                            <!-- <img src="images/logo-nav.png" alt=""> -->
                        </a>

                    </div>

                    <!-- TOP BAR -->
                    <div class="navbar-collapse collapse" id="stamp-navigation">

                        <!-- NAVIGATION LINK -->
                        <ul class="nav navbar-nav navbar-left main-navigation small-text">
                            <li><a href="${request.contextPath}/etiquettes">Etiquettes</a>
                            </li>
                            <li><a href="${request.contextPath}/#safety">Safety</a>
                            </li>
                            <li><a href="${request.contextPath}/#faq">FAQ</a>
                            </li>
                            <li><a href="${request.contextPath}/#about">About</a>
                            </li>
                        </ul>

                        <!-- LOGIN REGISTER -->
                        <ul class="nav navbar-nav navbar-right login-register small-text">
	                        <g:if test="${SecurityUtils.subject.principal != null}">
	                            <li class="login">
	                                <a href="${request.contextPath}/search"><i class="icon-basic-magnifier"></i> Search</a>
	                            </li>
	                            <li class="login">
	                                <a href="${request.contextPath}/journeys"><i class="icon-basic-bookmark"></i> My Requests</a>
	                            </li>
	                            <li class="login">
	                                <a href="${request.contextPath}/history"><i class="icon-basic-calendar"></i> History</a>
	                            </li>
	                            <li class="login dropdown">
	                                <a  class="dropdown-toggle" data-toggle="dropdown" href="#">
	                                    <i class="icon-man-people-streamline-user"></i> ${currentUser.profile.fullName}
	                                    <b class="caret"></b>
	                                </a>
	                                <ul class="dropdown-menu">
	                                	<n:hasRole name="${AdminsService.ADMIN_ROLE}">
	                                	<li>
											<g:link controller="admins" action="index" target="_blank">User Administration</g:link>
										</li>
										<%-- Need to change in future
										<li>
											<g:link controller="staticData" action="list">Static Pages</g:link>
										</li>
										 --%>
	                                	</n:hasRole>
	                                    <li>
	                                        <a href="${request.contextPath}/profile">Profile</a>
	                                    </li>
	                                    <li>
	                                        <a href="${request.contextPath}/password/change">Change Password</a>
	                                    </li>
	                                    <li>
	                                        <a href="${request.contextPath}/signout">Logout</a>
	                                    </li>
	                                </ul>
	                            </li>
	                        </g:if>
                            <n:notUser>
                            	<g:if test="${isSignin == null}">
	                            <li class="login js-login">
	                                <a href="${request.contextPath}/signin">Login</a>
	                            </li>
	                            </g:if>
	                            <g:if test="${isSignup == null}">
	                            <li class="register-button js-register inpage-scroll">
	                                <a href="${request.contextPath}/signup" class="navbar-register-button">Sign Up</a>
	                            </li>
	                            </g:if>
                            </n:notUser>

                        </ul>
                    </div>
                </div>
                <!-- /END CONTAINER -->
            </div>
            <!-- /END STICKY NAVIGATION -->
    	

   