//    This file is part of Penn TotalRecall <http://memory.psych.upenn.edu/TotalRecall>.
//
//    TotalRecall is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, version 3 only.
//
//    TotalRecall is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with TotalRecall.  If not, see <http://www.gnu.org/licenses/>.

package edu.upenn.psych.memory.precisionplayer;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Specification for an audio playback system appropriate for high-precision tasks, such as linguistic annotation.
 *
 * <h3>Overview</h3>
 * A PrecisionPlayer keeps track of two kinds of audio playback: main playback and short-interval playback.
 * Main playback is fully controlled, allowing stopping, changing of loudness, etc., and represents the user moving through an audio file.
 * Short interval playback, in contrast, cannot be stopped once started, nor is there any guarantee that loudness of short intervals can be changed once started. 
 * It is anticipated that users will replay short intervals around the main playback point as they make judgments about
 * their audio annotations.
 *
 * <h3>Goals</h3>
 * The overriding optimization goal of any implementation (after minimum guarantees are met) 
 * should be to minimize the latency of the play and stop methods, and to increase the accuracy of the stop method's 
 * return value.
 * 
 * <h3>Precision</h3>
 * Play methods are exact. That is to say, the frames they play meet the parameters of the method call exactly.
 * The stop method returns an approximate value, a concession to the realities of audio software and hardware buffering.
 * This is why there is no resume() function: in almost any implementation the resumption frame cannot be guaranteed identical
 * to the frame reported by the pause/stop method.
 * 
 * <h3>Error Handling</h3>
 * Implementations should handle exceptions internally, other than when the exceptions are declared thrown in this interface.
 * This is especially crucial for native code implementations.
 * Error reporting is handled through reporting <code>PrecisionPlayer.EventCode.ERROR</code> events to registered listeners.
 * 
 * <h3>Audio Formats</h3>
 * Implementations should strive to support at least the sampled sound audio types supported by Sun's reference implementation of
 * Java Sound: AIFF, AU, WAV, 8 or 16 bit samples, mono or stereo, 8KHz-48KHz sampling, with linear, a-law, or mu-law PCM.
 * Frames are identified with zero-based numbering.
 * 
 * <h4>Implementation-specific Behavior</h4>
 * Implementers should minimize the use of methods not defined here. 
 * Implementation-specific information should ideally be gathered by providing overloaded versions of the constructor, but a default 
 * constructor must always be provided.
 * 
 * @author Yuvi Masory
 */
public interface PrecisionPlayer {
	
	/**
	 * Status of the player.
	 * 
	 * <p><code>BUSY</code>: 
	 * Indicates the Player will not be able to minimize latency if one of the play functions is called now.
	 * Play calls will still succeed when Player is <code>BUSY</code>, but it may take longer than usual for the audio playback to begin.
	 * Status should be <code>BUSY</code> if <code>open()</code> has not yet been called.
	 * 
	 * <p><code>READY</code>:
	 * Indicates the Player is ready to play back audio with minimum latency.
	 * 
	 * <p><code>PLAYING</code>:
	 * Indicates that main playback is currently in progress.
	 */
	public static enum Status {BUSY, READY, PLAYING};
	
	/**
	 * Opens and initializes the provided file.
	 *
	 * <p>Any initialization an implementation needs to perform prior to main or short-interval playback should be performed now.
	 * After a file has been opened the <code>playAt()</code> and <code>playShortInterval()</code> methods should start audio playback with the smallest possible latency.
	 * 
	 * <p>May be implemented in-thread or concurrently. Concurrent implementations may choose to set status to <code>BUSY</code> while this method runs.
	 * However, <code>playAt()</code> must succeed regardless of whether the open thread is still running.
	 * 
	 * <p>Open is intended to be called only once per <code>PrecisionPlayer</code>. If it is called again, behavior is not guaranteed.
	 * 
	 * @param fileName The path of the audio file to be opened
	 * @throws FileNotFoundException If the provided file cannot be located
	 * @throws IOException If the provided file cannot be read
	 * @throws UnsupportedAudioFileException If the provided file is not of a supported format
	 */
	public void open(String fileName) throws FileNotFoundException, IOException, UnsupportedAudioFileException;
	
	
	/**
	 * Starts main playback at provided frame. 
	 * 
	 * Has no effect if main playback is already in progress, i.e. if status is <code>PLAYINGK/code>, or if <code>open()</code> has not yet been called.
	 * 
	 * <p>Implementations should seek to reduce latency to a minimum.
	 * 
	 * <p>Must be implemented concurrently, i.e. method must return as soon as possible, not waiting for playback to finish.
	 * 
	 * @param frame The audio frame from which to start playback
	 * @throws IllegalArgumentException If the provided frame is not present the audio file
	 */
	public void playAt(long frame) throws IllegalArgumentException;
	
	public void playAt(long startFrame, long endFrame) throws IllegalArgumentException;
	
	
	/**
	 * Starts short-interval playback at <code>startFrame</code>, and stops playing at <code>endFrame</code> instead of at natural end of media.
	 * 
	 * Has No effect if main playback is in progress, i.e. if status is <code>PLAYING</code>, or if <code>open()</code> has not yet been called.
	 * 
	 * <p>Implementations should seek to reduce latency to a minimum.
	 * 
	 * <p>The <code>stop()</code> method should have no impact on short-interval playback. <code>setLoudness()</code> is not guaranteed to have any effect 
	 * on short-interval playback once started. That is an implementation-specific decision.
	 * Implementations should also consider that users will often play the same short interval many times consecutively.
	 * 
	 * <p>To assist implementations using Sun's implementation of Java Sound, the difference between startFrame and endFrame
	 * must be less than or equal to 1048576, the limit imposed by <code>com.sun.media.sound.MixerClip</code>.
	 * <code>MixerClip</code> also requires that frame size be no larger than 4, but this seems to be a general aspect of Sun's implementation,
	 * so that should not introduce a further restriction to Sun-based implementations.
	 * 
	 * <p>Must be implemented concurrently, i.e. method must return as soon as possible, not waiting for playback to finish.
	 * 
	 * @param startFrame The audio frame from which to start playback
	 * @param endFrame The audio frame from which to terminate playback
	 * @throws IllegalArgumentException If endFrame >= startFrame, or if either is not in the boundary of the audio file
	 */
	public void playShortInterval(long startFrame, long endFrame) throws IllegalArgumentException;

	
	/**
	 * Stops main playback and returns "hearing" frame.
	 * 
	 * Returns -1 if main playback is not in progress, i.e. if status is not <code>PLAYING</code>, or if <code>open()</code> has not yet been called.
	 * 
	 * <p>First and foremost, implementations should seek to be as accurate as possible in returning the current "hearing" frame.
	 * The hearing frame is defined to be the last frame the user perceived.
	 * Due to the nature of audio hardware and software buffering, exactness in determining the hearing frame is impossible.
	 * This interface does not specify any guaranteed level of exactness.
	 * 
	 * <p>Second, implementations should seek to minimize latency (in this case the time between the stop action and the end of audio playback).
	 * 
	 * <p>Must be implemented in-thread, so that when the method returns, all stopping code has already been run.
	 * 
	 * @return The "hearing" frame at the time playback is stopped or -1 if main playback is not in progress..
	 */
	public long stop();
	
	
	/**
	 * Gives warning that <code>playAt(int)</code> may soon be called.
	 * 
	 * <p>To assist an implementation's ability to reduce latency of <code>playAt(int)</code> calls, this method informs an implementation that 
	 * <code>playAt(frame)</code> may be may soon be called.
	 * There is no guarantee <code>playAt(frame)</code> will be called.
	 * An implementation need not do anything in this method, as <code>playAt(int)</code> should always work, although with possibly greater latency.
	 * 
	 * <p>Unlike <code>playAt(frame)</code>, does not throw an exception if <tt>frame</tt> is not within the audio file.
	 * 
	 * <p>May be implemented in-thread or concurrently.
	 * 
	 * @param frame The predicted argument to an upcoming <code>playAt(int)</code> call.
	 */
	public void queuePlayAt(long frame);
	
	
	/**
	 * Gives warning that <code>playInterval(startFrame, endFrame)</code> may soon be called.
	 * 
	 * <p>To assist an implementation's ability to reduce latency of <code>playInterval(int, int)</code> calls, this method informs an implementation that 
	 * <code>playInterval(startFrame, endFrame)</code> may soon be called.
	 * There is no guarantee <code>playInterval(startFrame, endFrame)</code> will be called.
	 * An implementation need not do anything in this method, as <code>playInterfval(int, int)</code> should always work, although with possibly greater latency.
	 * 
	 * <p>Unlike <code>playAt(frame)</code>, does not throw an exception if <tt>frame</tt> is not within the audio file.
	 * 
	 * <p>May be implemented in-thread or concurrently.
	 * 
	 * @param startFrame The predicted startFrame argument to the next <code>queueShortInterval(int, int)</code> call
	 * @param endFrame The predicted endFrame argument to the next <code>queueShortInterval(int, int)</code> call
	 */
	public void queueShortInterval(long startFrame, long endFrame);
	
	
	/**
	 * Adjusts the perceived loudness of main playback.
	 * 
	 * <p>The parameter values should be in a linear relationship with perceived loudness.
	 * For example, a parameter of 50 should sound twice as loud as 25.
	 * Can be called at any point, whether or not main playback is in progress, or <code>open()</code> has been called.
	 * 
	 * <p>May be implemented in-thread or concurrently.
	 * 
	 * @param loudness An integer between 0 and 100, where 0 is mute and 100 is maximum loudness
	 */
	public void  setLoudness(int loudness);
	
	
	/**
	 * Returns the perceived loudness of main playback.
	 * 
	 * <p>The parameter values should be in a linear relationship with perceived loudness.
	 * For example, a parameter of 50 should sound twice as loud as 25.
	 * Can be called at any point, whether or not main playback is in progress, or <code>open()</code> has been called.
	 * 
	 * <p>May be implemented in-thread or concurrently.
	 * 
	 * @return An integer between 0 and 100, where 0 is mute and 100 is maximum loudness
	 */
	public int getLoudness();
	
	/**
	 * Returns one of the status codes defined in this interface. 
	 * 
	 * See documentation of status code fields for more information.
	 * 
	 * @return One of the status enums defined in this interface
	 */
	public Status getStatus();
	
	/**
	 * Adds a <code>PrecisionListener</code> to receive notifications from this <code>PrecisionPlayer</code>.
	 * 
	 * Does nothing if argument is null.
	 * Does not take effect until the next play call.
	 * 
	 * @param listener The <code>PrecisionListener</code> to receive notifications from this <code>PrecisionPlayer</code>.
	 */
	public void addListener(PrecisionListener listener);
	
	/**
	 * Indicates whether <code>getLoudness()</code> and <code>setLoudness()</code> calls will be effective.
	 * 
	 * This is a concession to APIs like RtAudio that do not provide a way to change line gains short of manipulating the audio
	 * samples directly.
	 * 
	 * @return <code>true</code> iff this <code>PrecisionPlayer</code> supports adjustment and querying of playback loudness
	 */
	public boolean isLoudnessControlSupported();
}
