//
//  ContentView.swift
//  iOS Demo
//
//  Created by Brad Patras on 4/20/25.
//

import BasicRemoteConfigs
import SwiftUI

struct ContentView: View {
	let brc: BasicRemoteConfigs
	@State var text: String = "Hello, world!"

	var body: some View {
		VStack {
			Spacer()

			Text(text)
				.padding()
				.font(.caption)

			Spacer()

			Button(action: {
				text = "Fetching configs..."

				Task {
					do {
						try await brc.fetchConfigs(ignoreCache: false)
						text = brc.getKeys().joined(separator: "\n")
					} catch {
						text = error.localizedDescription
					}
				}
			}) {
				Text("Fetch configs")
			}

			Spacer()

			Button(action: {
				text = "Hello, world!"

				brc.clearCache()
			}) {
				Text("Clear configs")
			}

			Spacer()

			Text("local cache version: \(brc.version)")
		}
	}
}

extension Dictionary where Key == String, Value == Any {
	func prettyPrint() -> String {
		var string: String = ""
		if let data = try? JSONSerialization.data(withJSONObject: self, options: .prettyPrinted){
			if let nstr = NSString(data: data, encoding: String.Encoding.utf8.rawValue){
				string = nstr as String
			}
		}
		return string
	}
}
