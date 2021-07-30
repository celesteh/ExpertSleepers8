ES8 {
	classvar <serverOptions, <jack_outs, <jack_name;
	var <>not_immutable;

	*initClass{
		jack_name = "";
	}

	* options {|soptions, name = "ES8", limit=8|
		// set limit to 8 to exclude optical outs

		var inputs=0, outputs=0, options, type, property, count, num;

		jack_outs = [];

		jack_name = name;



		count = {|type| // count inputs and outputs
			(type == \input).if ({
				inputs = inputs +1;
			}, {
				(type == \output).if ({
					outputs = outputs +1;
				})
			});
		};


		soptions.notNil.if({
			serverOptions = soptions;
		});

		serverOptions.isNil.if({ // get default options if none specified
			serverOptions = Server.default.options;
		});



		SCJConnection.getproperties.keys.do({|key|
			//key.postln;
			property = SCJConnection.properties[key];
			//property.postln;
			type = property[0].asSymbol; // type is input or output
			//"^ES8.*".matchRegexp(key.asString).if({ // could have been .beginswith
			key.asString.beginsWith(jack_name).if({ // is this the module we want?

				num = key.asString.replace((jack_name++":playback_"),"");
				num = num.asInteger;
				(num <= limit) .if({ // maybe don't count the optical ports
					count.(type);
					jack_outs = jack_outs.add(key);
				})
			} , {
				count.(type);
			});

			//type.postln;

		});
		serverOptions.numInputBusChannels = outputs; // we get input from outputs
		serverOptions.numOutputBusChannels = inputs;

		^serverOptions;

	}

	* disconnectSC{ |server, scName = "SuperCollider:out_", action|

		var src_key, func;

		func = {

			SCJConnection.getallports;
			SCJConnection.getconnections.keys.do({|src|
				var dest_keys=[];
				var src_keys=[];

				src.asString.beginsWith(scName).if({
					src_key = SCJConnection.allports.findKeyForValue(src);
					SCJConnection.connections[src].do({|dest|
						dest_keys = dest_keys.add(SCJConnection.allports.findKeyForValue(dest));
						src_keys = src_keys.add(src_key);
					});
					SCJConnection.disconnect(src_keys, dest_keys);
				});
			});
		};

		server.notNil.if({
			server.waitForBoot({
				func.value;
				server.sync;
				action.value;
			});
		}, {
			func.value;
			action.value();
		});

	}

	*connectSC { |server, scName = "SuperCollider:out_", jack_name|
		var func;

		jack_name.notNil.if({
			name = jack_name;
		});

		func = {

			// this one is coming

		}

	}

	*new {
		^super.new.init();
	}

	init {
		not_immutable=true;
	}

}
