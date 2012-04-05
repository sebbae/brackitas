CodeMirror.defineMode("xquery", function(config, parserConfig) {
  var indentUnit = config.indentUnit,
      multiLineStrings = parserConfig.multiLineStrings;
  var isOperatorChar = /[\(\)=+\-*&%!?@\/]/;
  var isVariableChar = /^\$[A-Za-z0-9]$/;
  var curPunc;
  var wordSet = "after ancestor ancestor-or-self and as ascending assert attribute" +
		" before by case cast child comment comment declare default define descendant" +
		" descendant-or-self descending document-node element element else eq every except" +
		" external following following-sibling follows for function if import in instance" +
		" intersect let module namespace of only or order parent precedes" +
		" preceding preceding-sibling processing-instruction ref return returns satisfies" +
		" schema schema-element self some sortby stable text then to treat typeswitch union" +
		" variable version where xquery try catch ne lt le gt ge"; 
  
  var dataTypeSet = "node item xs:ENTITIES xs:ENTITY xs:ID xs:IDREF xs:IDREFS" +
  		" xs:NCName xs:NMTOKEN" +
  		" xs:NMTOKENS xs:Name xs:QName xs:anySimpleType xs:anyType xs:anyURI" +
  		" xs:base64Binary xs:boolean xs:byte xs:date xs:dateTime xs:dayTimeDuration" +
  		" xs:decimal xs:double xs:duration xs:float xs:gDay xs:gMonth xs:gMonthDay" +
  		" xs:gYear xs:gYearMonth xs:hexBinary xs:int xs:integer xs:language xs:long" +
  		" xs:negativeInteger xs:nonNegativeInteger xs:nonPositiveInteger" +
  		" xs:normalizedString xs:positiveInteger xs:short xs:string xs:time" +
  		" xs:token xs:unsignedByte xs:unsignedInt xs:unsignedLong xs:unsignedShort" +
  		" xs:untyped xs:untypedAtomic xs:yearMonthDuration";

  var functionSet = "bit:add-doc-to-collection bit:create-collection" +
  		" bit:drop-collection bit:eval bit:exist-collection bit:load-file" +
  		" bit:parse bit:put bit:put bit:silent bit:store-doc" +
  		" bit:every bit:some" +
  		" app:delete app:deploy app:exist app:generate" +
  		" app:get-names app:get-structure app:is-running app:terminate" +
  		" http:send-request" +
  		" session:clear session:get-attribute" +
  		" session:get-attribute-names session:get-creation-time" +
  		" session:get-last-accessed-time session:get-max-inactive-interval" +
  		" session:invalidate session:remove-attribute session:set-attribute" +
  		" session:set-max-inactive-interval" +
  		" rsc:delete rsc:rename rsc:upload" +
  		" bdb:create-cas-index bdb:create-cas-index bdb:create-cas-index" +
  		" bdb:create-name-index bdb:create-name-index bdb:create-path-index" +
  		" bdb:create-path-index bdb:set-isolation bdb:set-lockdepth" +
  		" xqfile:compile xqfile:create xqfile:delete xqfile:get-compilation-error" +
  		" xqfile:is-library xqfile:save req:get-attribute req:get-attribute-names" +
  		" req:get-cookie req:get-cookie-names req:get-parameter" +
  		" req:get-parameter-names req:is-multipart-content" +
  		" fn:QName fn:abs" +
  		" fn:adjust-date-to-timezone fn:adjust-date-to-timezone" +
  		" fn:adjust-dateTime-to-timezone fn:adjust-dateTime-to-timezone" +
  		" fn:adjust-time-to-timezone fn:adjust-time-to-timezone fn:avg" +
  		" fn:base-uri fn:base-uri fn:boolean fn:ceiling fn:codepoint-equal" +
  		" fn:codepoints-to-string fn:collection fn:collection fn:compare" +
  		" fn:compare fn:concat fn:contains fn:contains fn:count fn:current-date" +
  		" fn:current-dateTime fn:current-time fn:data fn:dateTime fn:day-from-date" +
  		" fn:day-from-dateTime fn:days-from-duration fn:deep-equal fn:deep-equal" +
  		" fn:default-collation fn:distinct-values fn:distinct-values fn:doc" +
  		" fn:doc-available fn:document-uri fn:empty fn:encode-for-uri" +
  		" fn:ends-with fn:ends-with fn:error fn:error fn:error fn:error" +
  		" fn:escape-html-uri fn:exactly-one fn:exists fn:false fn:floor" +
  		" fn:hours-from-dateTime fn:hours-from-duration fn:hours-from-time" +
  		" fn:implicit-timezone fn:index-of fn:index-of fn:index-of fn:index-of" +
  		" fn:insert-before fn:insert-before fn:iri-to-uri fn:local-name" +
  		" fn:local-name fn:lower-case fn:lower-case fn:matches fn:matches" +
  		" fn:max fn:max fn:min fn:min fn:minutes-from-dateTime" +
  		" fn:minutes-from-duration fn:minutes-from-time fn:month-from-date" +
  		" fn:month-from-dateTime fn:months-from-duration fn:name fn:name" +
  		" fn:nilled fn:node-name fn:normalize-space fn:normalize-space fn:not" +
  		" fn:number fn:number fn:one-or-more fn:remove fn:remove fn:replace" +
  		" fn:replace fn:resolve-uri fn:resolve-uri fn:reverse fn:reverse fn:root" +
  		" fn:root fn:round fn:round-half-to-even fn:round-half-to-even" +
  		" fn:seconds-from-dateTime fn:seconds-from-duration fn:seconds-from-time" +
  		" fn:starts-with fn:starts-with fn:string fn:string fn:string-join" +
  		" fn:string-length fn:string-length fn:string-to-codepoints fn:subsequence" +
  		" fn:subsequence fn:subsequence fn:subsequence fn:substring fn:substring" +
  		" fn:substring-after fn:substring-after fn:substring-before" +
  		" fn:substring-before fn:sum fn:sum fn:timezone-from-date" +
  		" fn:timezone-from-dateTime fn:timezone-from-time fn:tokenize" +
  		" fn:tokenize fn:trace fn:translate fn:translate fn:true fn:unordered" +
  		" fn:upper-case fn:upper-case fn:year-from-date fn:year-from-dateTime" +
  		" fn:years-from-duration fn:zero-or-one" +
  		" util:get-mime-type util:list-predefined-functions" +
  		" util:list-predefined-modules util:mk-dir util:plain-print" +
  		" io:readline io:writeline";
  		
  function words(str) {
		var obj = {}, words = str.split(" ");
		for (var i = 0; i < words.length; ++i) obj[words[i]] = true;
		return obj;
  }    
  
  function tokenBase(stream, state) {
    var ch = stream.next();
    if (ch == '"' || ch == "'") {
      state.tokenize = tokenString(ch);
      return state.tokenize(stream, state);
    }
    if (/\d/.test(ch)) {
      stream.eatWhile(/[\w\.]/);
      return "number";
    }
    if (ch == "(") {
      if (stream.eat(":")) {
        state.tokenize = tokenComment;
        return tokenComment(stream, state);
      }
    }
    if (/\$/.test(ch)) {
      stream.eatWhile(/[\w]/);    	
      return "variable";
    }    
    if (isOperatorChar.test(ch)) {
      stream.eatWhile(isOperatorChar);
      return "operator";
    }
    stream.eatWhile(/[\w\-\:]/);
    var cur = stream.current();
    if (words(dataTypeSet).propertyIsEnumerable(cur)) {
      return "datatype";
    } 
    if (words(wordSet).propertyIsEnumerable(cur)) {
      return "keyword";
    }
    if (words(functionSet).propertyIsEnumerable(cur)) {
      return "function";
    }
    if (cur.indexOf(":") != -1) {
      return "function";
    }
    return "word";
  }

  function tokenString(quote) {
    return function(stream, state) {
      var escaped = false, next, end = false;
      while ((next = stream.next()) != null) {
        if (next == quote && !escaped) {end = true; break;}
        escaped = !escaped && next == "\\";
      }
      if (end || !(escaped || multiLineStrings))
        state.tokenize = tokenBase;
      return "string";
    };
  }

  function tokenComment(stream, state) {
    var maybeEnd = false, ch;
    while (ch = stream.next()) {
      if (ch == ")" && maybeEnd) {
        state.tokenize = tokenBase;
        break;
      }
      maybeEnd = (ch == ":");
    }
    return "comment";
  }

  function Context(indented, column, type, align, prev) {
    this.indented = indented;
    this.column = column;
    this.type = type;
    this.align = align;
    this.prev = prev;
  }
  function pushContext(state, col, type) {
    return state.context = new Context(state.indented, col, type, null, state.context);
  }
  function popContext(state) {
    var t = state.context.type;
    if (t == ")" || t == "]" || t == "}")
      state.indented = state.context.indented;
    return state.context = state.context.prev;
  }

  // Interface

  return {
    startState: function(basecolumn) {
      return {
        tokenize: null,
        context: new Context((basecolumn || 0) - indentUnit, 0, "top", false),
        indented: 0,
        startOfLine: true
      };
    },

    token: function(stream, state) {
      var ctx = state.context;
      if (stream.sol()) {
        if (ctx.align == null) ctx.align = false;
        state.indented = stream.indentation();
        state.startOfLine = true;
      }
      if (stream.eatSpace()) return null;
      curPunc = null;
      var style = (state.tokenize || tokenBase)(stream, state);
      if (style == "comment" || style == "meta") return style;
      if (ctx.align == null) ctx.align = true;

      if ((curPunc == ";" || curPunc == ":") && ctx.type == "statement") popContext(state);
      else if (curPunc == "{") pushContext(state, stream.column(), "}");
      else if (curPunc == "[") pushContext(state, stream.column(), "]");
      else if (curPunc == "(") pushContext(state, stream.column(), ")");
      else if (curPunc == "}") {
        while (ctx.type == "statement") ctx = popContext(state);
        if (ctx.type == "}") ctx = popContext(state);
        while (ctx.type == "statement") ctx = popContext(state);
      }
      else if (curPunc == ctx.type) popContext(state);
      else if (ctx.type == "}" || ctx.type == "top" || (ctx.type == "statement" && curPunc == "newstatement"))
        pushContext(state, stream.column(), "statement");
      state.startOfLine = false;
      return style;
    },

    indent: function(state, textAfter) {
      if (state.tokenize != tokenBase && state.tokenize != null) return 0;
      var ctx = state.context, firstChar = textAfter && textAfter.charAt(0);
      if (ctx.type == "statement" && firstChar == "}") ctx = ctx.prev;
      var closing = firstChar == ctx.type;
      if (ctx.type == "statement") return ctx.indented + (firstChar == "{" ? 0 : indentUnit);
      else if (ctx.align) return ctx.column + (closing ? 0 : 1);
      else return ctx.indented + (closing ? 0 : indentUnit);
    },

    electricChars: "{}"
  };
});