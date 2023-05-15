using System;

namespace com.ravnaandtines.janus
{
	/// <summary>
	/// Summary description for BucketizedPipe.
	/// </summary>
	public class BucketizedPipe
	{
		public BucketizedPipe()
		{
			System.Collections.Queue q2 =
				new System.Collections.Queue();

			// make it thread safe
			q = System.Collections.Queue.Synchronized(q2);
		}

		private System.Collections.Queue q = null;
        private bool closed = false;

		public void Enqueue(byte[] consumed)
		{
			q.Enqueue(consumed);
		}

		public byte[] Dequeue()
		{
            if(closed)
                return null;

			if(q.Count == 0)
			{
				return null;
			}
			byte[] lead = (byte[])(q.Dequeue());
			while(null == lead)
			{
				if(0 == q.Count)
				{
					return null;
				}
				lead = (byte[])(q.Dequeue());
			}

            if(lead.Length == 0)
                closed = true;

			return lead;
		}

        public bool Closed
        {
            get
            {
                return closed;
            }
        }

        public int Count
        {
            get
            {
                return q.Count;
            }
        }
        
        public byte[] Peek()
        {
            if(q.Count == 0)
                return null;
            return (byte[]) q.Peek();
        }

	}
}
