(function () {
  function forEach(arr, f) {
    for (var i = 0, e = arr.length; i < e; ++i) f(arr[i]);
  }
  
  function arrayContains(arr, item) {
    if (!Array.prototype.indexOf) {
      var i = arr.length;
      while (i--) {
        if (arr[i] === item) {
          return true;
        }
      }
      return false;
    }
    return arr.indexOf(item) != -1;
  }
  
  CodeMirror.xqueryHint = function(editor) {
    // Find the token at the cursor
    var cur = editor.getCursor(), token = editor.getTokenAt(cur), tprop = token;
    // If it's not a 'word-style' token, ignore the token.
    if (!/^[\w\:\-$_]*$/.test(token.string)) {
      token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
                       className: token.string == ":" ? "property" : null};
    }
    // If it is a property, find out what it is a property of.
    while (tprop.className == "property") {
      tprop = editor.getTokenAt({line: cur.line, ch: tprop.start});
      if (!context) var context = [];
      context.push(tprop);
    }
    return {list: getCompletions(token, context),
            from: {line: cur.line, ch: token.start},
            to: {line: cur.line, ch: token.end}};
  }
  
  var keywords = ("after ancestor ancestor-or-self and app:delete app:deploy" +
  		" app:exist app:generate app:get-names app:get-structure app:is-running" +
  		" app:terminate as ascending assert attribute bdb:create-cas-index" +
  		" bdb:create-name-index bdb:create-path-index bdb:set-isolation" +
  		" bdb:set-lockdepth before bit:add-doc-to-collection bit:create-collection" +
  		" bit:drop-collection bit:eval bit:every bit:exist-collection bit:load-file" +
  		" bit:make-directory bit:parse bit:put bit:put bit:silent bit:some" +
  		" bit:store-doc by case cast catch child comment declare default define" +
  		" descendant descendant-or-self descending document-node element else eq" +
  		" every except external fn:abs fn:adjust-date-to-timezone" +
  		" fn:adjust-dateTime-to-timezone fn:adjust-time-to-timezone fn:avg" +
  		" fn:base-uri fn:boolean fn:ceiling fn:codepoint-equal fn:codepoints-to-string" +
  		" fn:collection fn:compare fn:concat fn:contains fn:count fn:current-date" +
  		" fn:current-dateTime fn:current-time fn:data fn:dateTime fn:day-from-date" +
  		" fn:day-from-dateTime fn:days-from-duration fn:deep-equal" +
  		" fn:default-collation fn:distinct-values fn:doc fn:doc-available" +
  		" fn:document-uri fn:empty fn:encode-for-uri fn:ends-with fn:error" +
  		" fn:escape-html-uri fn:exactly-one fn:exists fn:false fn:floor" +
  		" fn:hours-from-dateTime fn:hours-from-duration fn:hours-from-time" +
  		" fn:implicit-timezone fn:index-of fn:insert-before fn:iri-to-uri" +
  		" fn:local-name fn:lower-case fn:matches fn:max fn:min fn:minutes-from-dateTime" +
  		" fn:minutes-from-duration fn:minutes-from-time fn:month-from-date" +
  		" fn:month-from-dateTime fn:months-from-duration fn:name fn:nilled" +
  		" fn:node-name fn:normalize-space fn:not fn:number fn:one-or-more" +
  		" fn:QName fn:remove fn:replace fn:resolve-uri fn:reverse fn:root" +
  		" fn:round fn:round-half-to-even fn:seconds-from-dateTime" +
  		" fn:seconds-from-duration fn:seconds-from-time fn:starts-with" +
  		" fn:string fn:string-join fn:string-length fn:string-to-codepoints" +
  		" fn:subsequence fn:substring fn:substring-after fn:substring-before" +
  		" fn:sum fn:timezone-from-date fn:timezone-from-dateTime fn:timezone-from-time" +
  		" fn:tokenize fn:trace fn:translate fn:true fn:unordered" +
  		" fn:upper-case fn:year-from-date fn:year-from-dateTime" +
  		" fn:years-from-duration fn:zero-or-one following following-sibling follows" +
  		" for function ge gt http:send-request if import in instance intersect" +
  		" io:readline io:writeline item le let lt module namespace ne node of" +
  		" only or order parent precedes preceding preceding-sibling" +
  		" processing-instruction ref req:get-attribute req:get-attribute-names" +
  		" req:get-cookie req:get-cookie-names req:get-parameter req:get-parameter-names" +
  		" req:is-multipart-content return returns rsc:delete rsc:rename rsc:upload" +
  		" satisfies schema schema-element self session:clear session:get-attribute" +
  		" session:get-attribute-names session:get-creation-time" +
  		" session:get-last-accessed-time session:get-max-inactive-interval" +
  		" session:invalidate session:remove-attribute session:set-attribute" +
  		" session:set-max-inactive-interval some stable text then to treat try" +
  		" typeswitch union util:get-mime-type util:list-predefined-functions" +
  		" util:list-predefined-modules util:mk-dir util:plain-print variable" +
  		" version where xqfile:compile xqfile:create xqfile:delete" +
  		" xqfile:get-compilation-error xqfile:is-library xqfile:save xquery" +
  		" xs:anySimpleType xs:anyType xs:anyURI xs:base64Binary xs:boolean" +
  		" xs:byte xs:date xs:dateTime xs:dayTimeDuration xs:decimal xs:double" +
  		" xs:duration xs:ENTITIES xs:ENTITY xs:float xs:gDay xs:gMonth" +
  		" xs:gMonthDay xs:gYear xs:gYearMonth xs:hexBinary xs:ID xs:IDREF xs:IDREFS" +
  		" xs:int xs:integer xs:language xs:long xs:Name xs:NCName xs:negativeInteger" +
  		" xs:NMTOKEN xs:NMTOKENS xs:nonNegativeInteger xs:nonPositiveInteger" +
  		" xs:normalizedString xs:positiveInteger xs:QName xs:short xs:string" +
  		" xs:time xs:token xs:unsignedByte xs:unsignedInt xs:unsignedLong" +
  		" xs:unsignedShort xs:untyped xs:untypedAtomic xs:yearMonthDuration").split(" ");
  
  function getCompletions(token, context) {
    var found = [], start = token.string;
    function maybeAdd(str) {
      if (str.indexOf(start) == 0 && !arrayContains(found, str)) found.push(str);
    }
    forEach(keywords, maybeAdd);
    return found;
  }
})();
