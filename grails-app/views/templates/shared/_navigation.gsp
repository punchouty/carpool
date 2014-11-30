		<%@ page import="grails.plugin.nimble.core.AdminsService"%>	
		<%@ page import="org.apache.shiro.SecurityUtils"%>	
		<link href='http://fonts.googleapis.com/css?family=Cabin:400,600|Open+Sans:300,600,400' rel='stylesheet'>
			
		<!-- STICKY NAVIGATION -->
            <div class="navbar navbar-inverse bs-docs-nav navbar-fixed-top sticky-navigation" role="navigation">
                <div class="container">
                    <div class="navbar-header">

                        <!-- LOGO ON STICKY NAV BAR -->
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#stamp-navigation">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-grid-2x2"></span>
                        </button>

                        <!-- LOGO -->
                        <a class="navbar-brand logo-racloop" href="#">
                            RACLOOP
                            <!-- <img src="images/logo-nav.png" alt=""> -->
                        </a>

                    </div>

                    <!-- TOP BAR -->
                    <div class="navbar-collapse collapse" id="stamp-navigation">

                        <!-- NAVIGATION LINK -->
                        <ul class="nav navbar-nav navbar-left main-navigation small-text">
                            <li><a href="#section5">Etiquettes</a>
                            </li>
                            <li><a href="#section6">FAQ</a>
                            </li>
                            <li><a href="#section7">About</a>
                            </li>
                        </ul>

                        <!-- LOGIN REGISTER -->
                        <ul class="nav navbar-nav navbar-right login-register small-text">
                            <li class="login">
                                <a href="${request.contextPath}/main"><i class="icon-basic-magnifier"></i> Search</a>
                            </li>
                            <li class="login">
                                <a href="${request.contextPath}/journeys"><i class="icon-basic-bookmark"></i> My Requests</a>
                            </li>
                            <li class="login dropdown">
                                <a  class="dropdown-toggle" data-toggle="dropdown" href="#">
                                    <i class="icon-man-people-streamline-user"></i> Rajan Punchouty
                                    <b class="caret"></b>
                                </a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a href="#">Profile</a>
                                    </li>
                                    <li>
                                        <a href="#">Change Password</a>
                                    </li>
                                    <li>
                                        <a href="#">Logout</a>
                                    </li>
                                </ul>
                            </li>
                            
                            <li class="login js-login">
                                <a href="#">Login</a>
                            </li>
                            <li class="register-button js-register inpage-scroll">
                                <a href="#section11" class="navbar-register-button">Sign Up</a>
                            </li>
                            

                        </ul>
                    </div>
                </div>
                <!-- /END CONTAINER -->
            </div>
            <!-- /END STICKY NAVIGATION -->
    	

   