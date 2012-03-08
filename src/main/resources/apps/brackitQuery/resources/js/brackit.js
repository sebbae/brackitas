/*
 * Hide a single html object from the page.
 * @id: Id of the object to be hidden
 */
function HideContent(id) 
{
	if(id.length < 1) 
		return;
  	document.getElementById(id).style.display = "none";
}

/*
 * Show a single html object from the page.
 * @id: Id of the object to be showed
 */
function ShowContent(id)
{
	if(id.length < 1) 
		return;
	document.getElementById(id).style.display = "block";
}

/*
 * Hide multiples fields with the same initial name.
 * For example: id xx_1, xx_2, xx_3, ..., xx_n
 * @commonName: Initial part of the id, that's similar among the fields to be hidden. 
 * @maxNum: Maximal number of the field to be hidden.
 */
function HideMultipleIdFields(commonName, maxNum) 
{
	if(commonName.length < 1) 
		return;
	if(maxNum.length < 1) 
		return;
	for (i = 0; i <= maxNum; i++)
  	   	HideContent(commonName + i );
}

/*
 * Given a html Select object id, return which of the possible items is selected.
 * @selectId: Id of the select object
 */
function getSelectedItem(selectId)
{
	if(selectId.length < 1) 
		return;
	var selectmenu   = document.getElementById(selectId)
  	var chosenoption = selectmenu.options[selectmenu.selectedIndex]
  	return chosenoption;
}

/*
 * Given a html Select object id, show another object from the page that corresponds to the selected item
 * from the select.
 * @selectId: Id of the select object
 */
function ShowItemFromSelect(itemInitialName, selectId)
{
	if(itemInitialName.length < 1) 
		return;
	if(selectId.length < 1) 
		return;	
	var chosenOption = getSelectedItem(selectId);
  	ShowContent(itemInitialName + chosenOption.id);
}