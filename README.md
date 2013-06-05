Entity-System-RDBMS-Beta--Java-
===============================

This is a full, basic implementation of the Entity System described here:

http://entity-systems-wiki.t-machine.org/rdbms-beta

PLEASE NOTE: this ES is intended as a teaching example: it works, it follows the principles of Entity System design -- but it has ZERO OPTIMIZATION; don't use it as a benchmark for performance.

What is an Entity System?
-----

This is a "almost as simple as possible" implementation of the ideas in this series of blog posts:


Using this on real projects
-----

If your game is simple - e.g. Tetris / Bejewelled / etc - this might be good enough for your final, shipped game. Lots of small games have been written using this implementation - on a modern CPU, even a non-optimized ES is fast enough for simple games.

I've used it successfully even in Android 2.x games - it's fast enough to run on 2011-era mobile phones.

But for any major game, you'd want to optimize and extend it a great deal. It's a good start to get you up and running with development, but you should aim to replace / re-write / optimize it at some point.

License / Crediting
-----

MIT - you can use this freely, for pretty much anything, commercial or non-commercial, etc.

If you have lawyers, they can contact me and I'll waive any/all rights over the code - I want you to use it.

It would be *nice* if you add a link to the Entity Systems Wiki in your credits - but entirely up to you.
