
<%@ page import="de.bund.bfr.crisis.Delivery" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'delivery.label', default: 'Delivery')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-delivery" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-delivery" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list delivery">
			
				<g:if test="${deliveryInstance?.day}">
				<li class="fieldcontain">
					<span id="day-label" class="property-label"><g:message code="delivery.day.label" default="Day" /></span>
					
						<span class="property-value" aria-labelledby="day-label"><g:fieldValue bean="${deliveryInstance}" field="day"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.month}">
				<li class="fieldcontain">
					<span id="month-label" class="property-label"><g:message code="delivery.month.label" default="Month" /></span>
					
						<span class="property-value" aria-labelledby="month-label"><g:fieldValue bean="${deliveryInstance}" field="month"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.year}">
				<li class="fieldcontain">
					<span id="year-label" class="property-label"><g:message code="delivery.year.label" default="Year" /></span>
					
						<span class="property-value" aria-labelledby="year-label"><g:fieldValue bean="${deliveryInstance}" field="year"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.packagingUnits}">
				<li class="fieldcontain">
					<span id="packagingUnits-label" class="property-label"><g:message code="delivery.packagingUnits.label" default="Packaging Units" /></span>
					
						<span class="property-value" aria-labelledby="packagingUnits-label"><g:fieldValue bean="${deliveryInstance}" field="packagingUnits"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.amount}">
				<li class="fieldcontain">
					<span id="amount-label" class="property-label"><g:message code="delivery.amount.label" default="Amount" /></span>
					
						<span class="property-value" aria-labelledby="amount-label"><g:fieldValue bean="${deliveryInstance}" field="amount"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.comment}">
				<li class="fieldcontain">
					<span id="comment-label" class="property-label"><g:message code="delivery.comment.label" default="Comment" /></span>
					
						<span class="property-value" aria-labelledby="comment-label"><g:fieldValue bean="${deliveryInstance}" field="comment"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.endExplanation}">
				<li class="fieldcontain">
					<span id="endExplanation-label" class="property-label"><g:message code="delivery.endExplanation.label" default="End Explanation" /></span>
					
						<span class="property-value" aria-labelledby="endExplanation-label"><g:fieldValue bean="${deliveryInstance}" field="endExplanation"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.foodRecipes}">
				<li class="fieldcontain">
					<span id="foodRecipes-label" class="property-label"><g:message code="delivery.foodRecipes.label" default="Food Recipes" /></span>
					
						<g:each in="${deliveryInstance.foodRecipes}" var="f">
						<span class="property-value" aria-labelledby="foodRecipes-label"><g:link controller="foodRecipe" action="show" id="${f.id}">${f?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.furtherTraceback}">
				<li class="fieldcontain">
					<span id="furtherTraceback-label" class="property-label"><g:message code="delivery.furtherTraceback.label" default="Further Traceback" /></span>
					
						<span class="property-value" aria-labelledby="furtherTraceback-label"><g:formatBoolean boolean="${deliveryInstance?.furtherTraceback}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.isEnd}">
				<li class="fieldcontain">
					<span id="isEnd-label" class="property-label"><g:message code="delivery.isEnd.label" default="Is End" /></span>
					
						<span class="property-value" aria-labelledby="isEnd-label"><g:formatBoolean boolean="${deliveryInstance?.isEnd}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.lot}">
				<li class="fieldcontain">
					<span id="lot-label" class="property-label"><g:message code="delivery.lot.label" default="Lot" /></span>
					
						<span class="property-value" aria-labelledby="lot-label"><g:link controller="lot" action="show" id="${deliveryInstance?.lot?.id}">${deliveryInstance?.lot?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.packagingType}">
				<li class="fieldcontain">
					<span id="packagingType-label" class="property-label"><g:message code="delivery.packagingType.label" default="Packaging Type" /></span>
					
						<span class="property-value" aria-labelledby="packagingType-label"><g:fieldValue bean="${deliveryInstance}" field="packagingType"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.serial}">
				<li class="fieldcontain">
					<span id="serial-label" class="property-label"><g:message code="delivery.serial.label" default="Serial" /></span>
					
						<span class="property-value" aria-labelledby="serial-label"><g:link controller="serial" action="show" id="${deliveryInstance?.serial?.id}">${deliveryInstance?.serial?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${deliveryInstance?.unit}">
				<li class="fieldcontain">
					<span id="unit-label" class="property-label"><g:message code="delivery.unit.label" default="Unit" /></span>
					
						<span class="property-value" aria-labelledby="unit-label"><g:fieldValue bean="${deliveryInstance}" field="unit"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:deliveryInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${deliveryInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
