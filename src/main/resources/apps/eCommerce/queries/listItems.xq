let $content := 
	let 
		$master := doc('_master.xml')/xtc//dir[xs:string(@name)='/'] 
	return 
		<ul>
			{
				for 
					$i 
				in 
					$master/doc 
				return 
					let 
						$docN := fn:doc($i/@name) 
					return 
						<li>
						    <a href="./showItemForm.xq?itemName={$docN/item/data(name)}">{$docN/item/data(name)}</a>
							{$docN/item/data(description)}
						</li> 
			}
		</ul>
return
	util:template($content)