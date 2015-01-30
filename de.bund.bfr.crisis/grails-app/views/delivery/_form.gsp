<%@ page import="de.bund.bfr.crisis.Delivery" %>



<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'day', 'error')} ">
	<label for="day">
		<g:message code="delivery.day.label" default="Day" />
		
	</label>
	<g:select name="day" from="${1..31}" class="range" value="${fieldValue(bean: deliveryInstance, field: 'day')}" noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'month', 'error')} ">
	<label for="month">
		<g:message code="delivery.month.label" default="Month" />
		
	</label>
	<g:select name="month" from="${1..12}" class="range" value="${fieldValue(bean: deliveryInstance, field: 'month')}" noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'year', 'error')} ">
	<label for="year">
		<g:message code="delivery.year.label" default="Year" />
		
	</label>
	<g:select name="year" from="${1900..2100}" class="range" value="${fieldValue(bean: deliveryInstance, field: 'year')}" noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'packagingUnits', 'error')} required">
	<label for="packagingUnits">
		<g:message code="delivery.packagingUnits.label" default="Packaging Units" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="packagingUnits" value="${fieldValue(bean: deliveryInstance, field: 'packagingUnits')}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'amount', 'error')} required">
	<label for="amount">
		<g:message code="delivery.amount.label" default="Amount" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="amount" value="${fieldValue(bean: deliveryInstance, field: 'amount')}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'comment', 'error')} ">
	<label for="comment">
		<g:message code="delivery.comment.label" default="Comment" />
		
	</label>
	<g:textField name="comment" value="${deliveryInstance?.comment}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'endExplanation', 'error')} ">
	<label for="endExplanation">
		<g:message code="delivery.endExplanation.label" default="End Explanation" />
		
	</label>
	<g:textField name="endExplanation" value="${deliveryInstance?.endExplanation}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'foodRecipes', 'error')} ">
	<label for="foodRecipes">
		<g:message code="delivery.foodRecipes.label" default="Food Recipes" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${deliveryInstance?.foodRecipes?}" var="f">
    <li><g:link controller="foodRecipe" action="show" id="${f.id}">${f?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="foodRecipe" action="create" params="['delivery.id': deliveryInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'foodRecipe.label', default: 'FoodRecipe')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'furtherTraceback', 'error')} ">
	<label for="furtherTraceback">
		<g:message code="delivery.furtherTraceback.label" default="Further Traceback" />
		
	</label>
	<g:checkBox name="furtherTraceback" value="${deliveryInstance?.furtherTraceback}" />

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'isEnd', 'error')} ">
	<label for="isEnd">
		<g:message code="delivery.isEnd.label" default="Is End" />
		
	</label>
	<g:checkBox name="isEnd" value="${deliveryInstance?.isEnd}" />

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'lot', 'error')} ">
	<label for="lot">
		<g:message code="delivery.lot.label" default="Lot" />
		
	</label>
	<g:select id="lot" name="lot.id" from="${de.bund.bfr.crisis.Lot.list()}" optionKey="id" value="${deliveryInstance?.lot?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'packagingType', 'error')} ">
	<label for="packagingType">
		<g:message code="delivery.packagingType.label" default="Packaging Type" />
		
	</label>
	<g:textField name="packagingType" value="${deliveryInstance?.packagingType}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'serial', 'error')} ">
	<label for="serial">
		<g:message code="delivery.serial.label" default="Serial" />
		
	</label>
	<g:select id="serial" name="serial.id" from="${de.bund.bfr.crisis.Serial.list()}" optionKey="id" value="${deliveryInstance?.serial?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: deliveryInstance, field: 'unit', 'error')} ">
	<label for="unit">
		<g:message code="delivery.unit.label" default="Unit" />
		
	</label>
	<g:textField name="unit" value="${deliveryInstance?.unit}"/>

</div>

