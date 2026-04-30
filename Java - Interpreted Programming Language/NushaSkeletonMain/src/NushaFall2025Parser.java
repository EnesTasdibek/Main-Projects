import AST.*;

import java.awt.*;
import java.sql.Struct;
import java.util.Optional;
import java.util.LinkedList;
import java.util.List;

public class NushaFall2025Parser {
    public TokenManager tokenManager;

    public NushaFall2025Parser() {
        this.tokenManager = new TokenManager(new LinkedList<>());

    }
    //Nusha = Definitions Variables Rules
    public Optional<Nusha> Nusha(LinkedList<Token> tokens) throws SyntaxErrorException {
        Nusha parser = new Nusha();
        this.tokenManager = new TokenManager(tokens);

        Optional<Definitions> definitions = this.Definitions();

        if (!definitions.isEmpty()) {
            parser.definitions = definitions.get();
        }

        Optional<Variables> variables1 = this.Variables();
        if (variables1.isPresent()) {


            parser.variables = variables1.get();

        }
        Optional<Rules> rules1 = this.Rules();
        if(rules1.isPresent()) {

            parser.rules = rules1.get();

        }
        return (Optional.of(parser));
    }

//-------------------------------------------------------------------------
//    Definitions = Definition*
//    Definition = IDENTIFIER "=" (Choices | Struct ) NEWLINE
//    Choices = "{" IDENTIFIER ( "," IDENTIFIER )*  "}"
//    Struct = "[" Entry ( "," Entry )*  "]"
//    Entry = "unique"? IDENTIFIER IDENTIFIER
    private Optional<Definitions> Definitions() throws SyntaxErrorException {
        //LinkedList<Definition> definitionsList = new LinkedList<>();
        Definitions definitions = new Definitions();
        definitions.definition = new LinkedList<>();
        // Definition Definition1 = new Definition();
        //getting the variables
        while (true) {
            Optional<Definition> definition = this.Definition();
            if (definition.isPresent()) {
                definitions.definition.add(definition.get());

            } else {
                break;
            }
        }
        return Optional.of(definitions);
    }
    // Definition = IDENTIFIER "=" (Choices | Struct ) NEWLINE
    //
    // "Story = [unique Chef c, unique Dish d, unique City p]\n"+
    //  OR
    // "Name = {Brian, Matilda, Ursula}\n"+
    //
    // Name = variablename
    private Optional<Definition> Definition() throws SyntaxErrorException {
        Definitions definitions = new Definitions();
        Definition definition = new Definition();

        while (tokenManager.MatchAndRemove((Token.TokenTypes.NEWLINE)).isPresent()) {
        }
        Optional<Token> identifier = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);

        if (identifier.isEmpty()) {
           return Optional.empty();
         }
            definition.definitionName = identifier.get().Value.get();
            //EQUALS sign
            if (tokenManager.MatchAndRemove(Token.TokenTypes.EQUAL).isEmpty()) {
                throw new SyntaxErrorException("definition declaration needs EQUAlS sign", tokenManager.getLine(), tokenManager.getColumn());
            }
            Optional<Choices> choices =  this.Choices();
            Optional<NStruct> struct =  Optional.empty();

            //it sends empty struct that is why empty
            if (choices.isEmpty()) {

                struct = this.Struct();

            }
        if (choices.isEmpty() && struct.isEmpty()) {

            throw new SyntaxErrorException("no choices or struct detected", tokenManager.getLine(), tokenManager.getColumn());
        }
        definition.choices = choices;
        definition.nstruct = struct;

            while (tokenManager.MatchAndRemove((Token.TokenTypes.NEWLINE)).isPresent()) {

            }
            return Optional.of(definition);
    }

        //Choices = "{" IDENTIFIER ( "," IDENTIFIER )*  "}"
        // "Chef = {Ava, Diego, Mei, Sam}\n"+
        //chef = variablename, ava diego mei sam = choice
        private Optional<Choices> Choices () throws SyntaxErrorException {
            Choices choices = new Choices();
            choices.choice = new LinkedList<>();
            //Choice choice = new Choice();
            //implement while right brace isEmpty, store all identifiers inside
            //EQUALS sign
            while (!tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE).isEmpty()) {
            }
            Optional<Token> leftcurly = tokenManager.MatchAndRemove(Token.TokenTypes.LEFTCURLY);

            if (leftcurly.isEmpty()) {
                return Optional.empty();
            }
            Optional<Token> identifier = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);
            if (identifier.isEmpty()) {
                throw new SyntaxErrorException("identifier not found", tokenManager.getLine(), tokenManager.getColumn());
            }
            choices.choice.add(identifier.get().Value.get()); //added first guy in choices

                        while (true) {
                            tokenManager.MatchAndRemove(Token.TokenTypes.COMMA);

                            Optional<Token> identifier2 = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);
                            if (identifier2.isEmpty()) {
                                break;
                            }
                            choices.choice.add(identifier2.get().Value.get());

                        }
                        if(!tokenManager.MatchAndRemove(Token.TokenTypes.RIGHTCURLY).isPresent()) {
                            throw new SyntaxErrorException("right Curly not found", tokenManager.getLine(), tokenManager.getColumn());
                        }
                        while (tokenManager.MatchAndRemove((Token.TokenTypes.NEWLINE)).isPresent()) {}
                    return Optional.of(choices);

            //return Optional.of(choices);
        }
            //   Struct = "[" Entry ( "," Entry )*  "]"
            // "Story = [unique Chef c, unique Dish d, unique City p]\n"+
            //p=name, city =type and unique is unique
            private Optional<NStruct> Struct () throws SyntaxErrorException {
                while (tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE).isPresent()) {}
                NStruct struct = new NStruct();
                //Entry entry = new Entry();

                Optional<Token> leftbrace = tokenManager.MatchAndRemove(Token.TokenTypes.LEFTBRACE);
                //start

                if (leftbrace.isEmpty()) {
                    return Optional.empty();
                }

                Optional<Entry> entry1 = Entry();
                if (entry1.isEmpty()) {
                    //return Optional.empty();
                    throw new SyntaxErrorException("struct is empty", tokenManager.getLine(), tokenManager.getColumn());
                }
                struct.entry.add(entry1.get());
                // no struct

                    if (tokenManager.MatchAndRemove(Token.TokenTypes.COMMA).isEmpty()) {
                        throw new SyntaxErrorException("struct needs COMMA", tokenManager.getLine(), tokenManager.getColumn());
                    }

                    while (true) {
                        tokenManager.MatchAndRemove(Token.TokenTypes.COMMA);

                        //here to fix
                        Optional<Entry> entry2 = Entry();

                    if (entry2.isEmpty()) {

                        break;
                    }
                        struct.entry.add(entry2.get());

                    }

                    if (tokenManager.MatchAndRemove(Token.TokenTypes.RIGHTBRACE).isEmpty()) {
                        //return Optional.empty();
                        throw new SyntaxErrorException("struct needs RIGHTBRACE sign", tokenManager.getLine(), tokenManager.getColumn());
                    }
                while (tokenManager.MatchAndRemove((Token.TokenTypes.NEWLINE)).isPresent()) {}
                    return Optional.of(struct);

            }
            //    Entry = "unique"? IDENTIFIER IDENTIFIER
            //              unique Chef c
            // "Story = [unique Chef c, unique Dish d, unique City p]\n"+
            //p=name, city =type and unique is unique
            private Optional<Entry> Entry () throws SyntaxErrorException {
                Entry entry = new Entry();

                Optional<Token> unique = tokenManager.MatchAndRemove(Token.TokenTypes.UNIQUE);


                if (unique.isEmpty()) {
                    entry.unique = false;
                }
                 //(unique.get().Value.get().equals(tokenManager.getLine())) {}
                else {
                    entry.unique = true;
                }

                    Optional<Token> identifier = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);
                //that means if we get to a point where there is no entry, we return optinal empty to finish parsing
                    if (identifier.isEmpty()) {
                        return Optional.empty();
                        //throw new SyntaxErrorException("entry needs type", tokenManager.getLine(), tokenManager.getColumn());
                    }
                    entry.type = identifier.get().Value.get();
                    //entry.name = identifier.get().Value.get();
//second identifier is name
                    Optional<Token> identifier2 = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);
                    if (identifier2.isEmpty()) {
                        throw new SyntaxErrorException("entry needs name", tokenManager.getLine(), tokenManager.getColumn());
                    }
                    entry.name = identifier2.get().Value.get();
                    return Optional.of(entry);
                }
//---------------------------------------------------------------
            //Variables = Variable*
            private Optional<Variables> Variables () throws SyntaxErrorException {
                Variables variables = new Variables();
                Variable variables1 = new Variable();
                //getting the variables
                while (true) {
                    Optional<Variable> variable = this.Variable();
                    if (variable.isPresent()) {
                        variables.variable.add(variable.get());
                    } else {
                        break;
                    }
                }
                return Optional.of(variables);
            }
            //Variable = "var" IDENTIFIER ":" IDENTIFIER ( "[" NUMBER "]")? NEWLINE
            //"var Birds : Bird[6]
            //type, variablename, size
            private Optional<Variable> Variable () throws SyntaxErrorException {
                Optional<Token> tokennn = tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE);
                while (tokennn.isPresent()) {
                    tokennn = tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE);
                }
                Variable variable = new Variable();
                Optional<Token> var = tokenManager.MatchAndRemove(Token.TokenTypes.VAR);
                //START
                if (var.isPresent()) {
                    Optional<Token> varName = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);
                    if (varName.isEmpty()) {
                        throw new SyntaxErrorException("Variable name needed", tokenManager.getLine(), tokenManager.getColumn());
                    }
                    variable.variableName = varName.get().Value.get();
                    //COLON
                    if (tokenManager.MatchAndRemove(Token.TokenTypes.COLON).isEmpty()) {
                        throw new SyntaxErrorException("Variable declaration needs COLON", tokenManager.getLine(), tokenManager.getColumn());
                    }
                    //Type after COLON
                    Optional<Token> typeCOL = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);
                    if (typeCOL.isEmpty()) {
                        throw new SyntaxErrorException("Variable declaration needs Type after colon", tokenManager.getLine(), tokenManager.getColumn());
                    }
                    variable.type = typeCOL.get().Value.get();
                    //?OPTIONAL Braces
                    if (tokenManager.MatchAndRemove(Token.TokenTypes.LEFTBRACE).isPresent()) {
                        while (tokenManager.MatchAndRemove(Token.TokenTypes.RIGHTBRACE).isEmpty()) {
                            variable.size = tokenManager.MatchAndRemove(Token.TokenTypes.NUMBER).get().Value.get().describeConstable();
                        }
                    }
                    //NEWLINE
                    if (tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE).isEmpty()) {
                        throw new SyntaxErrorException("newline symbol needed for variable declaration", tokenManager.getLine(), tokenManager.getColumn());
                    }
                    return Optional.of(variable);
                    //If does not start with var, return empty
                    //Only parse var with VAR
                } else {
                    return Optional.empty();
                }
            }

            //Rules = Rule*
private Optional<Rules> Rules() throws SyntaxErrorException {
        Rules rules = new Rules();

                while (true) {
                    Optional<Rule> rule = this.Rule();
                    if (rule.isPresent()) {
                        rules.rule.add(rule.get());
                    } else {
                        break;
                    }
                }

       return Optional.of(rules);
            }
            //Rule = Expression ("=>" NEWLINE INDENT Expression* DEDENT)?
            // "Stories[0].a = Joanne\n"+
                //OR
            //"Stories.d = Sushi =>\n"+
            //               "	Stories.p != Rome\n"+
            //               "	Stories.p != Mumbai\n"+
            //               "	Stories.p != MexicoCity\n"+
            //               "\n"+
private Optional<Rule> Rule() throws SyntaxErrorException {



        while(tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE).isPresent()) {}

        Optional<Expression> expression = Expression();

    Rule rule = new Rule();
        //Rules rules = new Rules();

    if(expression.isPresent()) {
        rule.expression = expression.get();
        rule.thens = new LinkedList<>();


        if (tokenManager.MatchAndRemove(Token.TokenTypes.YIELDS).isPresent()) {

            tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE);

            if(tokenManager.MatchAndRemove(Token.TokenTypes.INDENT).isEmpty()){

                throw new SyntaxErrorException("indent needed for expression", tokenManager.getLine(), tokenManager.getColumn());
            }
            //this would mean that if we get to a point where there is no expression, it would break the loop.
            while (true) {

                Optional<Expression> expression3 = this.Expression();//add while

                if (expression3.isEmpty()) {
                    break;
                    //throw new SyntaxErrorException("expression is empty", tokenManager.getLine(), tokenManager.getColumn());
                }

                rule.thens.add(expression3.get());

            }

            tokenManager.MatchAndRemove(Token.TokenTypes.DEDENT);

        }
    }else{
        return Optional.empty();
    }

        return Optional.of(rule);
            }
            //Stories.d = Sushi
            //Expression = VariableReference Op VariableReference
            // "Stories[0].a = Joanne\n"+
            //OR
            //"Stories.d = Sushi
 private Optional<Expression> Expression() throws SyntaxErrorException {


        while(tokenManager.MatchAndRemove(Token.TokenTypes.NEWLINE).isPresent()) {}

            Optional<VariableReference> variableReference =  variableReference();

            Optional<Op> op =  Op();

            if(op.isEmpty()){
                return Optional.empty();

            }


               Optional<VariableReference> variableReference2 =  variableReference();

               if(variableReference2.isEmpty()){
                   return Optional.empty();
               }

     Expression expression = new Expression();

     expression.left = variableReference.get();
     expression.op = op.get();
                expression.right= variableReference2.get();


            return Optional.of(expression);
            }

            //Op = "=" | "!="
private Optional<Op> Op() throws SyntaxErrorException {

             //start
             Optional<Token> equal = tokenManager.MatchAndRemove(Token.TokenTypes.EQUAL);
                    //op.type

                if(equal.isPresent()) {
                    //Op.OpTypes.Equal.equals(equal.get().Value.get());
                    Op op = new Op();
                // Optional<Token> notEqual = tokenManager.MatchAndRemove(Token.TokenTypes.NOTEQUAL);
                 //if (notEqual.isEmpty()) {
                     //return Optional.empty();
                   //  throw new SyntaxErrorException("OP does not have both signs", tokenManager.getLine(), tokenManager.getColumn());
                 //}
                 //op.type= Op.OpTypes.valueOf(notEqual.get().Value.get());
                 op.type = Op.OpTypes.Equal;
                 //op.type.name().equals("=");
                 return Optional.of(op);

             }
                if(tokenManager.MatchAndRemove(Token.TokenTypes.NOTEQUAL).isPresent()) {

                    Op op = new Op();
                    op.type = Op.OpTypes.NotEqual;

                    return Optional.of(op);
                }

        return Optional.empty();
            }
            //VariableReference = IDENTIFIER  VRModifier?
            // "Stories[0].a = Joanne\n"+
            //OR
            //"Stories.d = Sushi
    private Optional<VariableReference> variableReference() throws SyntaxErrorException{



        Optional<Token> identifier = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);

        //start
        if(identifier.isPresent()) {
            VariableReference variableReference = new VariableReference();
            variableReference.variableName = identifier.get().Value.get();


            variableReference.vrmodifier = vrModifier();

            return Optional.of(variableReference);

        }
        else{

            return Optional.empty();

        }
            }

            //VRModifier = "." IDENTIFIER VRModifier? | ( "[" NUMBER "]") VRModifier?
//  "Days.m = Emoji =>\n"+
    //left variable name = days, left part =m, rightvariablename = Emohi
//               "    Days.b = Science\n"+
//               "\n"+
   private Optional<VRModifier> vrModifier() throws SyntaxErrorException{
        VRModifier vrModifier = new VRModifier();


        if(tokenManager.MatchAndRemove(Token.TokenTypes.DOT).isPresent()){

            Optional<Token> identifier = tokenManager.MatchAndRemove(Token.TokenTypes.IDENTIFIER);

            if (identifier.isEmpty()) {
                throw new SyntaxErrorException("identifier needed for vr modifier", tokenManager.getLine(), tokenManager.getColumn());
            }

           // if (vrModifier.vrmodifier.isPresent()) {

                vrModifier.dot = true;//.
                vrModifier.part = Optional.of(identifier.get().Value.get());//m
                vrModifier.vrmodifier = vrModifier();//vrmodifier
                return Optional.of(vrModifier);


            //return Optional.of(vrModifier);

        }

         if(tokenManager.MatchAndRemove(Token.TokenTypes.LEFTBRACE).isPresent()){

            //tokenManager.MatchAndRemove(Token.TokenTypes.NUMBER);

            Optional<Token> number= tokenManager.MatchAndRemove(Token.TokenTypes.NUMBER);

            tokenManager.MatchAndRemove(Token.TokenTypes.RIGHTBRACE);


            VRModifier vrModifier2 = new VRModifier();

             //if (vrModifier.vrmodifier.isPresent()) {
                vrModifier2.dot = false;

                vrModifier2.size = number.get().Value.get();

                vrModifier2.vrmodifier = vrModifier();

                return Optional.of(vrModifier2);
                //}
        }
        return Optional.of(vrModifier);
            }

        }


    /*
    Definitions = Definition*
    Definition = IDENTIFIER "=" (Choices | Struct ) NEWLINE
    Choices = "{" IDENTIFIER ( "," IDENTIFIER )*  "}"
    Struct = "[" Entry ( "," Entry )*  "]"
    Entry = "unique"? IDENTIFIER IDENTIFIER

    Variables = Variable*
    Variable = "var" IDENTIFIER ":" IDENTIFIER ( "[" NUMBER "]")? NEWLINE

    Rules = Rule*
    Rule = Expression ("=>" NEWLINE INDENT Expression* DEDENT)?
    Expression = VariableReference Op VariableReference
    Op = "=" | "!="
    VariableReference = IDENTIFIER  VRModifier?
    VRModifier = "." IDENTIFIER VRModifier? | ( "[" NUMBER "]") VRModifier?

     */
