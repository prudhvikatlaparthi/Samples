package com.pru.playground

fun main() {
    val data = prepareData()

    for (item in data) {
        val items = extractData(item)
        println(item.plus(" -> ").plus(items.toSet().joinToString(",")))
    }
}

private fun extractData(item: String): MutableList<String> {
    var actualCondition = item
    actualCondition = actualCondition.replace(" AND ", " && ")
    actualCondition = actualCondition.replace(" OR ", " || ")
    actualCondition = actualCondition.replace(" IS NOT", " != ")
    actualCondition = actualCondition.replace(" != ", " !$ ")
    actualCondition = actualCondition.replace(" >= ", " >$ ")
    actualCondition = actualCondition.replace(" <= ", " <$ ")
    actualCondition = actualCondition.replace(" = ", " == ")
    actualCondition = actualCondition.replace(" !$ ", " != ")
    actualCondition = actualCondition.replace(" <$ ", " <= ")

    var preCondition = actualCondition.replace(" && ", " $ ")
    preCondition = preCondition.replace(" || ", " $ ")

    preCondition = preCondition.replace(" == ", " ~ ")
    preCondition = preCondition.replace(" != ", " ~ ")
    preCondition = preCondition.replace(" >= ", " ~ ")
    preCondition = preCondition.replace(" <= ", " ~ ")


    val ls = preCondition.split(" $ ")
    val items = mutableListOf<String>()
    for (it in ls) {
        it.split(" ~ ").getOrNull(0)?.let {
            items.add(it)
        }
    }
    return items
}

const val data =
    "ProcessNo = 1\n" + "Process = 'Y'\n" + "BillingCycle = 1\n" + "Price <= 5000\n" + "EstimatedTaxAmount = '12'\n" + "PruningID = ''\n" + "UseofWaterwastDescription = '10'\n" + "Tax = 'JTAE'\n" + "IPA = 2\n" + "Address = 3\n" + "Active = 'Y'\n" + "Administrationoffice = 2\n" + "Administrationoffice = 2\n" + "Economicactivity = '2' OR Economicactivity = 1\n" + "Tax != ''\n" + "EconomicActivity = 'Industrial'\n" + "Text = 'vivek'\n" + "EstimatedAmount = 78\n" + "Tax != ''\n" + "ComboA = 'Bijouterie'\n" + "ModelNumber = '9090'\n" + "Combo = '0.90'\n" + "TotalTaxAmount = '22'\n" + "AdministrativeOffice = 2\n" + "LicenseIssued = 'Y'\n" + "PropertyID IS NOT NULL\n" + "Tax = 'QSISA' AND PropertyAge = 2\n" + "Property != ''\n" + "PropertyType = 2\n" + "PropertyID = NULL OR AssessmentValue <= 5 AND NewAssessmentValue != NULL\n" + "Date = '20-06-2023'"

fun prepareData(): List<String> {
    return data.split("\n")
}