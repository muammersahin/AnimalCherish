/*
 * MIT License
 *
 * Copyright (c) [2018] [Mark Tripoli (Triippz)]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.team1.animalproject.blockchain.queries;

import com.team1.animalproject.blockchain.ipfs.IpfsService;
import com.team1.animalproject.blockchain.utils.Connections;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import javax.print.attribute.standard.Compression;
import java.io.IOException;

public class Payment {

	public static String myToken = null;
	private KeyPair pair;

	public Payment(KeyPair pair) {
		this.pair = pair;
	}

	public SubmitTransactionResponse sendPayment(boolean isMainNet, KeyPair srcPair, String destination, String amount, String memo) throws IOException {
		Server server = Connections.getServer(isMainNet);

		/* we already have the user's pair, but now we need to get the destinations */
		KeyPair destPair = KeyPair.fromAccountId(destination);

		/* now lets make sure the account exists */
		server.accounts().account(destPair);

		/* if there was no error, lets grab the current information on YOUR account */
		AccountResponse sourceAccount = server.accounts().account(srcPair);

		/* build the tx */
		Transaction transaction = buildNativeTransaction(sourceAccount, destPair, amount, memo);

		// Sign it
		transaction.sign(srcPair);

		/* send it off to the network */
		return sendTransaction(transaction, server);

	}

	private Transaction buildNativeTransaction(AccountResponse sourceAccount, KeyPair destPair, String ammount, String memo) {
		return new Transaction.Builder(sourceAccount).addOperation(new PaymentOperation.Builder(destPair, new AssetTypeNative(), ammount).build())

				// A memo allows you to add your own metadata to a transaction. It's
				// optional and does not affect how Stellar treats the transaction.
				.addMemo(Memo.text(memo)).build();
	}

	private SubmitTransactionResponse sendTransaction(Transaction transaction, Server server) throws IOException {
		return server.submitTransaction(transaction);
	}

	public void send(String memo, KeyPair source) throws IOException {
		Network.useTestNetwork();
		Server server = new Server("https://horizon-testnet.stellar.org");

		KeyPair destination = KeyPair.fromAccountId("GBOOWLO3IC7TOQFPIAA3ERSYGLG4EK2JYLMWMTCOUGJ7IQMC6EY6HFNU");

		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or there was another error.
		server.accounts().account(destination);

		// If there was no error, load up-to-date information on your account.
		AccountResponse sourceAccount = server.accounts().account(source);

		// Start building the transaction.
		Transaction transaction = new Transaction.Builder(sourceAccount)
				.addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), "10").build())
				// A memo allows you to add your own metadata to a transaction. It's
				// optional and does not affect how Stellar treats the transaction.
				.addMemo(Memo.text(memo))
				// Wait a maximum of three minutes for the transaction
				.setTimeout(180)
				.build();
		// Sign the transaction to prove you are actually the person sending it.
		transaction.sign(source);

		// And finally, send it off to Stellar!
		try {
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			System.out.println("Success!");
			System.out.println(response);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			System.out.println(e.getMessage());
			// If the result is unknown (no response body, timeout etc.) we simply resubmit
			// already built transaction:
			// SubmitTransactionResponse response = server.submitTransaction(transaction);
		}
	}

	public KeyPair getPair() {
		return pair;
	}

	public void setPair(KeyPair pair) {
		this.pair = pair;
	}
}
